<%--
  Created by IntelliJ IDEA.
  User: Imre
  Date: 2014.03.22.
  Time: 11:06
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:main>
    <div id="leftboxes">
        <div class="postBox profilebox">
            <img src="${pageContext.request.contextPath}/img/profile/${user.getPicture()}">
            <h2>${user.getName()}</h2><br>
            <b>since:</b> <span class="smallgrey">${user.getTimeOfRegistration()}</span><br>
            <b>posts:</b> <span class="smallgrey">${postsCount}</span><br>
            <b>followers:</b> <span class="smallgrey">${followersCount}</span><br>
            <b>following:</b> <span class="smallgrey">${followingsCount}</span>
        </div>
    </div>

    <c:choose>
        <c:when test="${following}">
            <c:forEach var="currentUser" items="${usersToShow}">
                <div class="jumbotron followbox">
                    <img src="${pageContext.request.contextPath}/img/profile/${currentUser.getPicture()}"> <a href="timeline?uid=${currentUser.getId()}">@${currentUser.getName()}</a>
                    <form method="post" action="follow?action=unfollow&uid=${currentUser.getId()}&source=following">
                        <input type="submit" class="btn btn-default navbar-btn submitpost" value="Unfollow">
                    </form>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <c:forEach var="currentUser" items="${usersToShow}">
                <div class="jumbotron followbox">
                    <img src="${pageContext.request.contextPath}/img/profile/${currentUser.getPicture()}"> ${currentUser.getName()}
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>



</t:main>