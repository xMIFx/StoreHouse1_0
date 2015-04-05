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
    <meta charset="utf-8">
    <title>Your First WebSocket!</title>
   <script src="${pageContext.request.contextPath}/jsFiles/sendMessages.js"></script>
    <script src="${pageContext.request.contextPath}/jsFiles/showHideFunction.js"></script>
   <link href="${pageContext.request.contextPath}/pages/UserStyle.css" rel="stylesheet" type="text/css" >
</head>

<body>
<div class="center">
    <div class="user_box">
         <div class="group">
            <div class="open"></div>
             <a href="#" onclick="functionShowHide('1')" class="gr">group 1</a>
            <ul id="1">
                <li class="online"><a href=""><span></span>Name1</a></li>
                <li class="online"><a href=""><span></span>Name2</a></li>
                <li class="offline"><a href=""><span></span>Name3</a></li>

            </ul>
        </div>
        <div class="group">
            <a href="#" class="gr">group 2</a>
            <ul>
                <li class="offline"><a href=""><span></span>Name4</a></li>
                <li class="online"><a href=""><span></span>Name5</a></li>
                <li class="offline"><a href=""><span></span>Name6</a></li>

            </ul>
        </div>
    </div>
<div class="output_box">
<h1 style="text-align: center;">Hello World WebSocket Client</h1>

    <div style="text-align: center;">
        <form action="">
            <input onclick="send_message()" value="Send" type="button">
            <input id="textID" name="message" value="Hello WebSocket!" type="text">
        </form>
    </div>
    <div id="output"></div>
</div>
</div>
</body>
</html>
