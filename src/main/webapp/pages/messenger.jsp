<%--
  Created by IntelliJ IDEA.
  User: bukatinvv
  Date: 02.04.2015
  Time: 13:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>

<head>
    <title></title>
   <script src="${pageContext.request.contextPath}/jsFiles/sendMessages.js"></script>
</head>

<body>
<meta charset="utf-8">
<title>Your First WebSocket!</title>

<h1 style="text-align: center;">Hello World WebSocket Client</h1>
    <br>
    <div style="text-align: center;">
        <form action="">
            <input onclick="send_message()" value="Send" type="button">
            <input id="textID" name="message" value="Hello WebSocket!" type="text"><br>
        </form>
    </div>
    <div id="output"></div>
</body>
</html>
