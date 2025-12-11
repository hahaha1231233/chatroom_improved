<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>聊天室 V2</title>
    <style>
        /* 样式代码保持不变 */
    </style>
</head>
<body>
<div class="header">
    <h2>在线聊天室 V2 - 欢迎：<c:out value="${currentUser}" default="游客" /></h2>
</div>

<div class="container">
    <div class="chat-area">
        <h3>聊天记录</h3>
        <div id="messages">
            <c:forEach var="msg" items="${messages}">
                <c:choose>
                    <c:when test="${msg.type == 'SYSTEM'}">
                        <div class="message system">
                            <div>
                                <span class="sender"><c:out value="${msg.sender}" />：</span>
                                <!-- 关键修改：msg.time 改为 msg.date -->
                                <span class="time"><fmt:formatDate value="${msg.date}" pattern="HH:mm:ss" /></span>
                            </div>
                            <div class="content"><c:out value="${msg.content}" /></div>
                        </div>
                    </c:when>
                    <c:when test="${msg.type == 'PRIVATE'}">
                        <div class="message private">
                            <div>
                                <span class="sender"><c:out value="${msg.sender}" /> → <c:out value="${msg.targetUser}" />：</span>
                                <!-- 关键修改：msg.time 改为 msg.date -->
                                <span class="time"><fmt:formatDate value="${msg.date}" pattern="HH:mm:ss" /></span>
                                <span class="private-indicator">[私聊]</span>
                            </div>
                            <div class="content"><c:out value="${msg.content}" /></div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="message public">
                            <div>
                                <span class="sender"><c:out value="${msg.sender}" />：</span>
                                <!-- 关键修改：msg.time 改为 msg.date -->
                                <span class="time"><fmt:formatDate value="${msg.date}" pattern="HH:mm:ss" /></span>
                            </div>
                            <div class="content"><c:out value="${msg.content}" /></div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </div>
    </div>

    <div class="users-area">
        <h3>在线用户 <span class="user-count">(<c:out value="${fn:length(users)}" />人)</span></h3>
        <ul class="user-list">
            <c:forEach var="user" items="${users}">
                <li>
                    <span><c:out value="${user}" /></span>
                    <button class="private-chat-btn" onclick="setPrivateChat('${user}')">私聊</button>
                </li>
            </c:forEach>
        </ul>
    </div>
</div>

<form class="message-form" action="${pageContext.request.contextPath}/chat" method="post">
    <select id="targetUser" name="targetUser">
        <option value="">公共聊天</option>
        <c:forEach var="user" items="${users}">
            <option value="${user}"><c:out value="${user}" /> (私聊)</option>
        </c:forEach>
    </select>
    <input type="text" name="message" placeholder="输入消息..." required
           maxlength="200" autocomplete="off" id="messageInput">
    <button type="submit">发送</button>
</form>

<form class="logout-form" action="${pageContext.request.contextPath}/logout" method="post">
    <button type="submit">退出聊天室</button>
</form>

<script>
    // 自动滚动到底部
    window.onload = function() {
        const messagesDiv = document.getElementById('messages');
        if (messagesDiv) {
            messagesDiv.scrollTop = messagesDiv.scrollHeight;
        }
    };

    // 设置私聊目标
    function setPrivateChat(username) {
        document.getElementById('targetUser').value = username;
        document.getElementById('messageInput').focus();
        document.getElementById('messageInput').placeholder = "私聊消息给 " + username + "...";
    }

    // 清除私聊选择
    document.getElementById('targetUser').addEventListener('change', function() {
        if (this.value === '') {
            document.getElementById('messageInput').placeholder = "输入消息...";
        }
    });

    // 定期刷新页面获取新消息（每10秒）
    setInterval(function() {
        location.reload();
    }, 10000);
</script>
</body>
</html>