<%@tag description="Simple Wrapper Tag" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>JReTwitter</title>

    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/img/favicon.png">

    <link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet" type="text/css">
    <link href="${pageContext.request.contextPath}/css/bootstrap-theme.css" rel="stylesheet" type="text/css">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet" type="text/css">
</head>

<body>

<!-- Fixed navbar -->
<div class="navbar navbar-default navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="index">JReTwitter</a>
        </div>

        <c:choose>
            <c:when test="${user==null}">

                <div class="navbar-collapse collapse">
                    <ul class="nav navbar-nav">
                        <li><a href="index">Home</a></li>
                        <li><a href="about">About</a></li>
                    </ul>
                    <ul class="nav navbar-nav navbar-right">
                        <li><a href="auth?action=login">Login</a></li>
                        <li><a href="register">Register</a></li>
                    </ul>
                </div>

            </c:when>
            <c:otherwise>

                <div class="navbar-collapse collapse">
                    <ul class="nav navbar-nav">
                        <li><a href="timeline?uid=${user.getId()}&show=all">Timeline</a></li>
                        <li><a href="timeline?uid=-1">Explore</a></li>
                        <li><a href="about">About</a></li>

                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">Others<b class="caret"></b></a>
                            <ul class="dropdown-menu">
                                <li class="dropdown-header">Connections</li>
                                <li class="divider"></li>
                                <li><a href="follow?action=followers">Followers</a></li>
                                <li><a href="follow?action=following">Following</a></li>
                            </ul>
                        </li>
                    </ul>
                    <ul class="nav navbar-nav navbar-right">
                        <li><a href="timeline?uid=${user.getId()}&show=me">${user.getName()}</a></li>
                        <li><a href="auth?action=logout">Log out</a></li>
                    </ul>
                </div>

            </c:otherwise>
        </c:choose>

    </div>
</div>

<!-- Content -->
<div class="container">
    <jsp:doBody/>
</div>



<!-- Placed at the end of the document so the pages load faster -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>

</body>
</html>