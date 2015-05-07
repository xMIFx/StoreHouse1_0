<%--
  Created by IntelliJ IDEA.
  User: bukatinvv
  Date: 02.04.2015
  Time: 13:49
  To change this template use File | Settings | File Templates.
--%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>

<head>
    <meta charset="utf-8">
    <title>Your First WebSocket!</title>
    <script src="${pageContext.request.contextPath}/jsFiles/sendMessages.js"></script>
    <script src="${pageContext.request.contextPath}/jsFiles/showHideFunction.js"></script>
    <link href="${pageContext.request.contextPath}/cssFiles/messangerStyle.css" rel="stylesheet" type="text/css">
</head>

<body>
<a href="index.do">main</a>
<div class="center">
    <div class="user_box">
        <c:forEach var="entry" items="${groupMap}">
            <div class="group">
                <div class="open"></div>
                <a href="javascript:" class="gr"
                   onclick="functionAnimatedShowHide('${entry.key.ID}')">${entry.key.name}</a>
                <ul id="${entry.key.ID}" class="slide-down">
                    <c:forEach var="chatUser" items="${entry.value}">
                        <li id="user_${chatUser.id}"
                            class=${chatUser.online?"online":"offline"}><a href="javascript:"
                                                                           onclick="functionChangingChat('${chatUser.cryptUUID}')">
                            <span></span>${chatUser.name}</a><p class="MessageCount"></p>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:forEach>
    </div>

    <div id = "output_box" class="output_box" onload="initOutput()">
        <h1 style="text-align: center;">Hello World WebSocket Client</h1>

        <div style="text-align: center;">
            <form action="">
                <input onclick="send_message()" value="Send" type="button">
                <input id="textID" name="message" value="Hello WebSocket!" type="text">
            </form>
        </div>
        <div id="usersChat_0"></div>
    </div>
    <div class="information_about_chat" id="information_about_chat">
        <br style="text-align: right;">information about current chat</br>
    </div>
</div>
</body>
</html>
