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

<h1 class="glav">Messages</h1>

<div class="center">
    <div class="user_box">
        <c:forEach var="entry" items="${groupMap}">
            <div class="group">
                <div class="open"></div>
                <a href="javascript:" class="gr"
                   onclick="functionAnimatedShowHide('${entry.key.ID}')">${entry.key.name}</a>

                <p class="MessageCount"></p>
                <ul id="${entry.key.ID}" class="slide-down">
                    <c:forEach var="chatUser" items="${entry.value}">
                        <li id="user_${chatUser.id}"
                            class="${chatUser.online?"online":"offline"} user_ch"><a href="javascript:"
                                                                                     onclick="functionChangingChat('${chatUser.cryptUUID}','${chatUser.id}')">
                            <span></span>${chatUser.name}</a>

                            <p class="MessageCount"></p>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:forEach>
        <div class="group">
            <div class="open"></div>
            <a href="javascript:" class="gr"
               onclick="functionAnimatedShowHide('Other_chat')">Other chat</a>

            <p class="MessageCount"></p>
            <ul id="Other_chat" class="slide-down">
                <c:forEach var="bigChat" items="${bigChats}">
                    <li id="bigChat_${bigChat.idChat}"
                        class="user_ch"><a href="javascript:"
                                           onclick="functionChangingChatByID('${bigChat.idChat}')">
                        <span></span>${bigChat.nameChat}</a>

                        <p class="MessageCount"></p>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
    <div id="output_box" class="output_box" onload="initOutput()">


        <div class="chat_box" id="usersChat_0"  onscroll="functionOnScrollChat(this)"></div>
        <div style="text-align: center;">
            <form action="">
                <input onclick="send_message()" value="Send" type="button">
                <textarea id="textID" name="message" value="Hello WebSocket!" type="text"></textarea>
            </form>
        </div>
    </div>
    <div class="information_about_chat" id="information_about_chat">
        <br style="text-align: right;">information about current chat</br>
    </div>
    <div class="lastChats">Last chats</div>
    <div class="lastChats" id="lastChats">
        <c:forEach var="lastChat" items="${lastChats}">
            <c:if test="${lastChat.userList.size()>1}">
                <li id="lastChat_${lastChat.idChat}"
                    class="user_ch"><a href="javascript:"
                                       onclick="functionChangingChatByID('${lastChat.idChat}')">
                    <span></span>${lastChat.nameChat}</a>
                </li>
            </c:if>
            <c:if test="${lastChat.userList.size()==1}">
                <c:forEach var="lastUser" items="${lastChat.userList}">
                    <li id="lastUser_${lastUser.id}"
                        class="user_ch"><a href="javascript:"
                                           onclick="functionChangingChat('${lastUser.cryptUUID}','${lastUser.id}')">
                        <span></span>${lastUser.name}</a>
                    </li>
                </c:forEach>

            </c:if>

        </c:forEach>
    </div>
</div>
</body>
</html>
