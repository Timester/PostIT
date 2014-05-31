package net.talqum.postit.servlet;

import net.talqum.postit.domain.User;
import net.talqum.postit.persistence.RedisPersistence;
import net.talqum.postit.persistence.RedisPersistenceDefault;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Imre on 2014.03.22..
 */
@WebServlet( name="AuthServlet", urlPatterns = "/auth")
public class AuthServlet extends HttpServlet {

    private RedisPersistence persistenceBean = new RedisPersistenceDefault();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");

        User u = persistenceBean.authUser(name, password);

        if (u != null) {
            request.getSession().setAttribute("user", u);
            response.sendRedirect("timeline?uid=" + u.getId() + "&show=all");
        }
        else
        {
            RequestDispatcher rq = request.getRequestDispatcher("/WEB-INF/login.jsp");
            rq.forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        RequestDispatcher rq;

        switch (action) {
            case "login":
                rq = request.getRequestDispatcher("/WEB-INF/login.jsp");
                break;
            case "logout":
                request.getSession().invalidate();
                rq = request.getRequestDispatcher("/WEB-INF/index.jsp");
                break;
            default:
                rq = request.getRequestDispatcher("/WEB-INF/index.jsp");
                break;
        }

        rq.forward(request, response);
    }
}
