package com.chatroom_improved_version.servlet;

import com.chatroom_improved_version.model.Message;
import com.chatroom_improved_version.model.PrivateMessage;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@WebServlet("/private-chat")
public class PrivateChatServlet extends HttpServlet {

    @SuppressWarnings("unchecked")
    private List<Message> getMessageList(ServletContext context) {
        Object messages = context.getAttribute("messages");
        if (messages == null) {
            List<Message> list = new ArrayList<>();
            context.setAttribute("messages", list);
            return list;
        }
        return (List<Message>) messages;
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<PrivateMessage>> getPrivateMessages(ServletContext context) {
        Object privateMessages = context.getAttribute("privateMessages");
        if (privateMessages == null) {
            Map<String, List<PrivateMessage>> map = new HashMap<>();
            context.setAttribute("privateMessages", map);
            return map;
        }
        return (Map<String, List<PrivateMessage>>) privateMessages;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getOnlineUsers(ServletContext context) {
        Object users = context.getAttribute("onlineUsers");
        if (users == null) {
            Map<String, String> map = new LinkedHashMap<>();
            context.setAttribute("onlineUsers", map);
            return map;
        }
        return (Map<String, String>) users;
    }

    private void addPrivateMessage(String sender, String receiver, String content, ServletContext context) {
        Map<String, List<PrivateMessage>> privateMessages = getPrivateMessages(context);
        String conversationKey = getConversationKey(sender, receiver);

        synchronized (privateMessages) {
            List<PrivateMessage> messages = privateMessages.getOrDefault(conversationKey, new ArrayList<>());

            // 限制私聊历史记录为最近的50条
            if (messages.size() > 50) {
                messages.remove(0);
            }

            messages.add(new PrivateMessage(sender, receiver, content, LocalDateTime.now()));
            privateMessages.put(conversationKey, messages);
        }
    }

    private List<PrivateMessage> getPrivateConversation(String user1, String user2, ServletContext context) {
        Map<String, List<PrivateMessage>> privateMessages = getPrivateMessages(context);
        String conversationKey = getConversationKey(user1, user2);

        synchronized (privateMessages) {
            List<PrivateMessage> messages = privateMessages.get(conversationKey);
            if (messages == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(messages);
        }
    }

    private String getConversationKey(String user1, String user2) {
        // 确保对话键的顺序一致，无论谁先谁后
        if (user1.compareTo(user2) < 0) {
            return user1 + ":" + user2;
        } else {
            return user2 + ":" + user1;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String currentUser = (String) session.getAttribute("username");
        String targetUser = request.getParameter("target");

        // 如果没有指定私聊目标，重定向到主聊天室
        if (targetUser == null || targetUser.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/chat");
            return;
        }

        targetUser = targetUser.trim();

        // 检查目标用户是否在线
        Map<String, String> onlineUsers = getOnlineUsers(getServletContext());
        if (!onlineUsers.containsKey(targetUser)) {
            request.setAttribute("error", "用户 " + targetUser + " 不在线");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/chat");
            dispatcher.forward(request, response);
            return;
        }

        // 检查是否尝试与自己私聊
        if (currentUser.equals(targetUser)) {
            request.setAttribute("error", "不能与自己私聊");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/chat");
            dispatcher.forward(request, response);
            return;
        }

        // 获取私聊历史记录
        List<PrivateMessage> conversation = getPrivateConversation(currentUser, targetUser, getServletContext());

        // 标记消息为已读
        synchronized (conversation) {
            for (PrivateMessage msg : conversation) {
                if (msg.getReceiver().equals(currentUser) && !msg.isRead()) {
                    msg.markAsRead();
                }
            }
        }

        // 获取在线用户列表（排除当前用户）
        List<String> otherUsers = new ArrayList<>();
        synchronized (onlineUsers) {
            for (String user : onlineUsers.keySet()) {
                if (!user.equals(currentUser)) {
                    otherUsers.add(user);
                }
            }
        }

        // 设置请求属性
        request.setAttribute("currentUser", currentUser);
        request.setAttribute("targetUser", targetUser);
        request.setAttribute("conversation", conversation);
        request.setAttribute("users", otherUsers);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/private-chat.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String currentUser = (String) session.getAttribute("username");
        String targetUser = request.getParameter("targetUser");
        String content = request.getParameter("message");
        String action = request.getParameter("action");

        if ("send".equals(action) && targetUser != null && content != null && !content.trim().isEmpty()) {
            targetUser = targetUser.trim();
            content = content.trim();

            // 检查目标用户是否在线
            Map<String, String> onlineUsers = getOnlineUsers(getServletContext());
            if (!onlineUsers.containsKey(targetUser)) {
                request.setAttribute("error", "用户 " + targetUser + " 已离线，消息发送失败");
                response.sendRedirect(request.getContextPath() + "/private-chat?target=" + targetUser);
                return;
            }

            // 添加到私聊历史
            addPrivateMessage(currentUser, targetUser, content, getServletContext());

            // 同时添加到主消息列表（带私聊标识）
            List<Message> messageList = getMessageList(getServletContext());
            synchronized (messageList) {
                if (messageList.size() > 100) {
                    messageList.remove(0);
                }
                messageList.add(new Message(currentUser, targetUser, content, LocalDateTime.now()));
            }

            // 发送成功，重定向回私聊页面
            response.sendRedirect(request.getContextPath() + "/private-chat?target=" + targetUser);
            return;
        } else if ("clear".equals(action) && targetUser != null) {
            // 清空对话历史
            Map<String, List<PrivateMessage>> privateMessages = getPrivateMessages(getServletContext());
            String conversationKey = getConversationKey(currentUser, targetUser.trim());

            synchronized (privateMessages) {
                privateMessages.remove(conversationKey);
            }

            response.sendRedirect(request.getContextPath() + "/private-chat?target=" + targetUser);
            return;
        }

        // 如果没有指定action或参数不全，重定向到主聊天室
        response.sendRedirect(request.getContextPath() + "/chat");
    }
}