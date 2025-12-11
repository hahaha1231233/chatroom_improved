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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");

        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("error", "请输入昵称");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
            dispatcher.forward(request, response);
            return;
        }

        String trimmed = username.trim();

        // 检查用户名是否已存在
        Map<String, String> onlineUsers = getOnlineUsers(getServletContext());
        synchronized (onlineUsers) {
            if (onlineUsers.containsKey(trimmed)) {
                request.setAttribute("error", "该昵称已被使用，请选择其他昵称");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
                dispatcher.forward(request, response);
                return;
            }
        }

        HttpSession session = request.getSession();
        session.setAttribute("username", trimmed);

        // 添加用户到在线列表
        onlineUsers.put(trimmed, session.getId());

        // 添加系统消息：用户加入
        List<Message> messageList = getMessageList(getServletContext());
        synchronized (messageList) {
            if (messageList.size() > 100) {
                messageList.remove(0);
            }
            messageList.add(new Message(trimmed + " 加入了聊天室", LocalDateTime.now()));
        }

        response.sendRedirect(request.getContextPath() + "/chat");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
}