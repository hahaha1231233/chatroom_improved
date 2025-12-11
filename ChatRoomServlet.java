package com.chatroom_improved_version.servlet;

import com.chatroom_improved_version.model.Message;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/chat")
public class ChatRoomServlet extends HttpServlet {

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
    private Map<String, String> getOnlineUsers(ServletContext context) {
        Object users = context.getAttribute("onlineUsers");
        if (users == null) {
            Map<String, String> map = new LinkedHashMap<>();
            context.setAttribute("onlineUsers", map);
            return map;
        }
        return (Map<String, String>) users;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        ServletContext context = getServletContext();
        List<Message> messageList = getMessageList(context);
        Map<String, String> onlineUsers = getOnlineUsers(context);

        // 获取当前用户名
        String currentUser = (String) session.getAttribute("username");

        // 过滤消息：只显示公共消息和系统消息，以及当前用户的私聊消息
        List<Message> filteredMessages = new ArrayList<>();
        synchronized (messageList) {
            for (Message msg : messageList) {
                if (msg.isPublic() || msg.isSystem() ||
                        (msg.isPrivate() && (msg.getSender().equals(currentUser) ||
                                msg.getTargetUser().equals(currentUser)))) {
                    filteredMessages.add(msg);
                }
            }
        }

        // 修改后的代码：
// 创建在线用户列表（包含所有用户）
        List<String> allUsers = new ArrayList<>();
        synchronized (onlineUsers) {
            allUsers.addAll(onlineUsers.keySet());
        }

// 更新请求属性
        request.setAttribute("messages", filteredMessages);
        request.setAttribute("users", allUsers);  // 使用 allUsers 而不是 otherUsers
        request.setAttribute("currentUser", currentUser);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/chat.jsp");
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

        String username = (String) session.getAttribute("username");
        String content = request.getParameter("message");
        String targetUser = request.getParameter("targetUser"); // 私聊目标用户

        if (content != null && !content.trim().isEmpty()) {
            List<Message> messageList = getMessageList(getServletContext());
            synchronized (messageList) {
                // 保持最后100条消息
                if (messageList.size() > 100) {
                    messageList.remove(0);
                }

                if (targetUser != null && !targetUser.trim().isEmpty()) {
                    // 私聊消息
                    messageList.add(new Message(username, targetUser.trim(), content.trim(), LocalDateTime.now()));
                } else {
                    // 公共消息
                    messageList.add(new Message(username, content.trim(), LocalDateTime.now()));
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/chat");
    }
}