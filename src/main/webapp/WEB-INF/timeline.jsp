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
            <img src="${pageContext.request.contextPath}/img/profile/${targetUser.getPicture()}">
            <h2>${targetUser.getName()}</h2><br>
            <b>since:</b> <span class="smallgrey">${targetUser.getTimeOfRegistration()}</span><br>
            <b>posts:</b> <span class="smallgrey">${postsCount}</span><br>
            <b>followers:</b> <span class="smallgrey">${followersCount}</span><br>
            <b>following:</b> <span class="smallgrey">${followingsCount}</span>
        </div>
        <c:choose>
            <c:when test="${isMe or explore}">
                <div class="postBox">
                    <form action="timeline" method="post" id="sendPost">
                        <textarea name="text" id="text" placeholder="What's on your mind?" autofocus></textarea><br>
                        <input type="submit" class="btn btn-default navbar-btn submitpost">
                    </form>
                </div>
            </c:when>
            <c:otherwise>
                <c:if test="${not explore}">
                    <c:choose>
                        <c:when test="${isFollowed}">
                            <div class="postBox">
                                <form method="post" action="follow?action=unfollow&uid=${targetUser.getId()}&source=timeline">
                                    <input type="submit" class="btn btn-default navbar-btn submitpost" value="Unfollow">
                                </form>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="postBox">
                                <form method="post" action="follow?action=follow&uid=${targetUser.getId()}&source=timeline">
                                    <input type="submit" class="btn btn-default navbar-btn submitpost" value="Follow">
                                </form>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </c:otherwise>
        </c:choose>

    </div>


    <c:forEach var="post" items="${postsToShow}">
        <div class="jumbotron post">
            <img src="${pageContext.request.contextPath}/img/profile/${post.getOwner().getPicture()}">
            <div class="postcontent">
                <a href="timeline?uid=${post.getOwnerId()}">@${post.getOwner().getName()}</a> - <span class="smallgrey">${post.getCreated()}</span> <br>
                    ${post.getText()}
            </div>
        </div>
    </c:forEach>
</t:main>