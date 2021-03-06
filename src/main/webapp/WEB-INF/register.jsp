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
    <div class="jumbotron">
        <h1>Register</h1>
        <form action="register" method="post" enctype="multipart/form-data">
            <div class="formrow">
                <label>Username</label><input id="name" type="text" name="name" autofocus>
            </div>
            <div class="formrow">
                <label>Password</label><input id="password" type="password" name="password">
            </div>
            <div class="formrow">
                <label>Profile picture</label><input type="file" name="file" size="60" />
            </div>
            <div class="formrow">
                <input type="submit" class="btn btn-default navbar-btn">
            </div>
        </form>
    </div>
</t:main>