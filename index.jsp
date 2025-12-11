<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>在线聊天室登录 - V2</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 400px;
            margin: 50px auto;
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            background-color: #f8f9fa;
        }
        h2 {
            color: #333;
            text-align: center;
            margin-bottom: 30px;
        }
        form {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }
        label {
            font-weight: bold;
            color: #555;
        }
        input[type="text"] {
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            width: 100%;
            box-sizing: border-box;
            font-size: 16px;
        }
        button {
            background-color: #4CAF50;
            color: white;
            padding: 12px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            font-weight: bold;
            transition: background-color 0.3s;
        }
        button:hover {
            background-color: #45a049;
        }
        .error {
            color: #f44336;
            text-align: center;
            margin-top: 15px;
            padding: 10px;
            background-color: #ffebee;
            border-radius: 4px;
            border: 1px solid #ffcdd2;
        }
        .version {
            text-align: center;
            color: #666;
            margin-top: 20px;
            font-size: 14px;
        }
    </style>
</head>
<body>
<h2>聊天室 V2 - 登录</h2>
<form action="${pageContext.request.contextPath}/login" method="post">
    <label for="username">昵称：</label>
    <input type="text" id="username" name="username"
           placeholder="请输入您的昵称(2-20字符)"
           required minlength="2" maxlength="20"
           autocomplete="off" autofocus>
    <button type="submit">进入聊天室</button>
</form>
<div class="error">
    ${requestScope.error}
</div>
<div class="version">
    版本：2.0 | 功能：公共聊天 + 私聊 + 用户进出通知
</div>
</body>
</html>