package net.talqum.postit.servlet;

import net.talqum.postit.domain.User;
import net.talqum.postit.persistence.RedisPersistence;
import net.talqum.postit.persistence.RedisPersistenceDefault;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Imre on 2014.03.22..
 */
@WebServlet( name="FollowServlet", urlPatterns = "/follow")
public class FollowServlet extends HttpServlet {

    private RedisPersistence persistenceBean = new RedisPersistenceDefault();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String uidS = request.getParameter("uid");
        String source = request.getParameter("source");
        Long uid = Long.parseLong(uidS);
        User current = ((User)(request.getSession().getAttribute("user")));

        if(action != null && action.equals("follow")){
            persistenceBean.addFollowing(current, uid);
        }
        else if(action != null && action.equals("unfollow")){
            persistenceBean.removeFollowing(current, uid);
        }

        if(source != null && source.equals("following")){
            response.sendRedirect("follow?action=following");
        }
        else if(source != null && source.equals("timeline")){
            response.sendRedirect("timeline?uid=" + uid + "&show=all");
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        List<User> users = null;
        Boolean following = null;
        User current = ((User)(request.getSession().getAttribute("user")));

        if(action != null && action.equals("followers")){
            users = persistenceBean.getFollowers(current.getId());
            following = Boolean.FALSE;
        }
        else if (action != null && action.equals("following")){
            users = persistenceBean.getFollowing(current.getId());
            following = Boolean.TRUE;
        }
        else{
            RequestDispatcher rq = request.getRequestDispatcher("/WEB-INF/timeline.jsp");
            rq.forward(request, response);
        }

        request.setAttribute("following", following);
        request.setAttribute("usersToShow", users);

        // STATS
        Long followersCount = persistenceBean.getFollowersCount(current.getId());
        Long followingsCount = persistenceBean.getFollowingsCount(current.getId());
        Long postsCount = persistenceBean.getPostsCount(current.getId());
        request.setAttribute("postsCount", postsCount);
        request.setAttribute("followersCount", followersCount);
        request.setAttribute("followingsCount", followingsCount);

        RequestDispatcher rq = request.getRequestDispatcher("/WEB-INF/follow.jsp");
        rq.forward(request, response);
    }

}