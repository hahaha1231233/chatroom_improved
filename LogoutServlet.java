package com.chatroom_improved_version.servlet;

import com.chatroom_improved_version.model.Message;
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

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute("username");
            if (username != null) {
                // 从在线用户列表移除
                Map<String, String> onlineUsers = getOnlineUsers(getServletContext());
                synchronized (onlineUsers) {
                    onlineUsers.remove(username);
                }

                // 添加系统消息：用户离开
                List<Message> messageList = getMessageList(getServletContext());
                synchronized (messageList) {
                    if (messageList.size() > 100) {
                        messageList.remove(0);
                    }
                    messageList.add(new Message(username + " 离开了聊天室", LocalDateTime.now()));
                }
            }
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}