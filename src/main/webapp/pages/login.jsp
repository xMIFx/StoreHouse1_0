<%--
  Created by IntelliJ IDEA.
  User: Vlad
  Date: 17.02.2015
  Time: 22:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <link href="${pageContext.request.contextPath}/pages/UserStyle.css" rel="stylesheet" type="text/css" >
</head>
<body>
<form method="post" action="/authorization.do">
<div class="autoriz">
    <input type="text" class="login" placeholder="login" name="login"/>
    <input type="password" class="password" placeholder="password" name="password"/>
    <div class="box"><input type="submit" value="ok" class="button1"/></div>

</div>
</form>

</body>
</html>
