package net.talqum.postit.servlet;

import net.talqum.postit.domain.User;
import net.talqum.postit.persistence.RedisPersistence;
import net.talqum.postit.persistence.RedisPersistenceDefault;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Created by Imre on 2014.03.22..
 */
@WebServlet( name="RegistrationServlet", urlPatterns = "/register")
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
                 maxFileSize=1024*1024*5,      // 5MB
                 maxRequestSize=1024*1024*10)   // 10MB
public class RegistrationServlet extends HttpServlet {

    /**
     * Name of the directory where uploaded files will be saved, relative to
     * the web application directory.
     */
    private static final String SAVE_DIR = "img/profile";

    private RedisPersistence persistenceBean = new RedisPersistenceDefault();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");

        // gets absolute path of the web application
        String appPath = request.getServletContext().getRealPath("");
        // constructs path of the directory to save uploaded file
        String savePath = appPath + File.separator + SAVE_DIR;

        // creates the save directory if it does not exists
        File fileSaveDir = new File(savePath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdir();
        }

        String fileName = "gw2.jpeg";                   // default pic
        Collection<Part> parts = request.getParts();
        for (Part part : parts) {
            if(part.getName().equals("file")) {
                // fileName = name; //extractFileName(part);
                fileName = part.getSubmittedFileName();
                part.write(savePath + File.separator + fileName);
            }
        }

        User u = new User();
        u.setName(name);
        u.setPassword(password);
        u.setTimeOfRegistration(LocalDateTime.now());
        u.setPicture(fileName);

        persistenceBean.addUser(u);

        request.getSession().setAttribute("user", u);

        response.sendRedirect("timeline?uid=" + u.getId() + "&show=all");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rq = request.getRequestDispatcher("/WEB-INF/register.jsp");
        rq.forward(request, response);
    }

}
