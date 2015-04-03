<%--
  Created by IntelliJ IDEA.
  User: Vlad
  Date: 07.03.2015
  Time: 23:44
  To change this template use File | Settings | File Templates.
--%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<c:if test="${user.uuid == user.getEmptyUUID()}">
    <a href="/authorization.do" class="txt">login</a>
</c:if>
<c:if test="${user.uuid != user.getEmptyUUID()}">
    <p>${user.name}</p>
    <a href="exit.do" class="txt">logOut</a>
</c:if>
<a href="messenger.do" class="txt">messages</a></div>
<ul>
    <c:forEach var="menu" items="${menuList}">

        <li style="padding-bottom: 10px;">
            <a href="menu.do?id=${menu.iD}"
               style="color: #000;text-decoration: none">${menu.name}</a>
        </li>
    </c:forEach>
</ul>
<ul>
    <c:forEach var="button" items="${buttonList}">

        <li style="padding-bottom: 10px;">
            <a href="${button.action}"
               style="color: #000;text-decoration: none">${button.name}</a>
        </li>
    </c:forEach>
</ul>
</body>
</html>
