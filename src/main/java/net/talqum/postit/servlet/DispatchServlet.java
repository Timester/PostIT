package net.talqum.postit.servlet;

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
@WebServlet( name="DispatchServlet", urlPatterns = {"/index", "/about"})
public class DispatchServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getRequestURL().toString().contains("about")){
            RequestDispatcher rq = request.getRequestDispatcher("/WEB-INF/about.jsp");
            rq.forward(request, response);
            return;
        }
        else {
            RequestDispatcher rq = request.getRequestDispatcher("/WEB-INF/index.jsp");
            rq.forward(request, response);
        }
    }

}
