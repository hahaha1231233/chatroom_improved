<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>私聊 - 聊天室 V2</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        .header {
            background: linear-gradient(135deg, #2196F3, #1976D2);
            color: white;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .container {
            display: flex;
            gap: 20px;
            margin-bottom: 20px;
        }
        .private-chat-area {
            flex: 3;
            background: white;
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            height: 500px;
            overflow-y: auto;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        .users-area {
            flex: 1;
            background: white;
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            height: 500px;
            overflow-y: auto;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        .message {
            margin-bottom: 15px;
            padding: 12px;
            border-radius: 6px;
            max-width: 80%;
        }
        .message.sent {
            background-color: #e3f2fd;
            margin-left: auto;
            border-left: 4px solid #2196F3;
        }
        .message.received {
            background-color: #f5f5f5;
            margin-right: auto;
            border-left: 4px solid #4CAF50;
        }
        .message .sender {
            font-weight: bold;
            font-size: 0.9em;
            margin-bottom: 5px;
        }
        .message.sent .sender {
            color: #2196F3;
            text-align: right;
        }
        .message.received .sender {
            color: #4CAF50;
        }
        .message .time {
            color: #888;
            font-size: 0.8em;
            margin-top: 5px;
        }
        .message.sent .time {
            text-align: right;
        }
        .message .content {
            color: #333;
            line-height: 1.4;
        }
        .private-indicator {
            background-color: #2196F3;
            color: white;
            padding: 2px 6px;
            border-radius: 3px;
            font-size: 0.8em;
            margin-left: 5px;
        }
        .user-list {
            list-style: none;
            padding: 0;
        }
        .user-list li {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px;
            margin-bottom: 8px;
            background-color: #f8f9fa;
            border-radius: 4px;
            border: 1px solid #e9ecef;
        }
        .user-list li.active {
            background-color: #e3f2fd;
            border-color: #2196F3;
        }
        .chat-btn {
            background-color: #2196F3;
            color: white;
            border: none;
            padding: 5px 10px;
            border-radius: 3px;
            cursor: pointer;
            font-size: 12px;
        }
        .chat-btn:hover {
            background-color: #1976D2;
        }
        .message-form {
            display: flex;
            gap: 10px;
            margin-top: 20px;
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        .message-form input {
            flex: 1;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        .message-form button {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: bold;
        }
        .btn-send {
            background-color: #2196F3;
            color: white;
        }
        .btn-send:hover {
            background-color: #1976D2;
        }
        .btn-clear {
            background-color: #f44336;
            color: white;
        }
        .btn-clear:hover {
            background-color: #d32f2f;
        }
        .navigation {
            display: flex;
            justify-content: space-between;
            margin-bottom: 20px;
        }
        .nav-btn {
            background-color: #4CAF50;
            color: white;
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        .nav-btn:hover {
            background-color: #45a049;
        }
        .conversation-info {
            background-color: #fff8e1;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
            text-align: center;
            border-left: 4px solid #FF9800;
        }
        .no-messages {
            text-align: center;
            color: #999;
            padding: 40px;
            font-style: italic;
        }
        .unread-indicator {
            background-color: #ff4444;
            color: white;
            border-radius: 50%;
            width: 8px;
            height: 8px;
            display: inline-block;
            margin-left: 5px;
        }
    </style>
</head>
<body>
<div class="header">
    <h2>私聊会话：<c:out value="${currentUser}" /> ↔ <c:out value="${targetUser}" /></h2>
</div>

<div class="navigation">
    <a href="${pageContext.request.contextPath}/chat" class="nav-btn">返回公共聊天室</a>
    <span>
        <form action="${pageContext.request.contextPath}/private-chat" method="post" style="display: inline;">
            <input type="hidden" name="targetUser" value="${targetUser}">
            <input type="hidden" name="action" value="clear">
            <button type="submit" class="btn-clear" onclick="return confirm('确定要清空与 ${targetUser} 的聊天记录吗？')">
                清空聊天记录
            </button>
        </form>
    </span>
</div>

<div class="container">
    <div class="private-chat-area">
        <div class="conversation-info">
            这是您与 <strong><c:out value="${targetUser}" /></strong> 的私聊会话
            <span class="private-indicator">私密聊天</span>
        </div>

        <div id="privateMessages">
            <c:choose>
                <c:when test="${empty conversation}">
                    <div class="no-messages">
                        还没有聊天记录，开始您的对话吧！
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="msg" items="${conversation}">
                        <div class="message <c:choose><c:when test="${msg.sender eq currentUser}">sent</c:when><c:otherwise>received</c:otherwise></c:choose>">
                            <div class="sender">
                                <c:out value="${msg.sender}" />
                                <c:if test="${msg.sender eq currentUser}">
                                    <span class="private-indicator">我</span>
                                </c:if>
                            </div>
                            <div class="content"><c:out value="${msg.content}" /></div>
                            <div class="time">
                                <!-- 关键修改：将 msg.time 改为 msg.date -->
                                <fmt:formatDate value="${msg.date}" pattern="HH:mm:ss" />
                                <c:if test="${msg.receiver eq currentUser and not msg.read}">
                                    <span class="unread-indicator" title="未读"></span>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="users-area">
        <h3>在线用户 <span class="user-count">(<c:out value="${users.size()}" />人)</span></h3>
        <ul class="user-list">
            <c:forEach var="user" items="${users}">
                <li <c:if test="${user eq targetUser}">class="active"</c:if>>
                    <span><c:out value="${user}" /></span>
                    <a href="${pageContext.request.contextPath}/private-chat?target=${user}" class="chat-btn">
                        <c:choose>
                            <c:when test="${user eq targetUser}">当前</c:when>
                            <c:otherwise>私聊</c:otherwise>
                        </c:choose>
                    </a>
                </li>
            </c:forEach>
        </ul>
    </div>
</div>

<form class="message-form" action="${pageContext.request.contextPath}/private-chat" method="post">
    <input type="hidden" name="targetUser" value="${targetUser}">
    <input type="hidden" name="action" value="send">
    <input type="text" name="message" placeholder="输入私聊消息..." required
           maxlength="500" autocomplete="off" id="messageInput" autofocus>
    <button type="submit" class="btn-send">发送</button>
</form>

<script>
    // 自动滚动到底部
    window.onload = function() {
        const messagesDiv = document.getElementById('privateMessages');
        if (messagesDiv) {
            messagesDiv.scrollTop = messagesDiv.scrollHeight;
        }
        document.getElementById('messageInput').focus();
    };

    // 自动刷新私聊页面（每5秒）
    setInterval(function() {
        location.reload();
    }, 5000);

    // 回车键发送消息
    document.getElementById('messageInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            if (this.value.trim() !== '') {
                this.form.submit();
            }
        }
    });
</script>
</body>
</html>