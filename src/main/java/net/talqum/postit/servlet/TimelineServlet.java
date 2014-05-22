package net.talqum.postit.servlet;

import net.talqum.postit.domain.Post;
import net.talqum.postit.domain.User;
import net.talqum.postit.persistence.RedisPersistence;
import net.talqum.postit.persistence.RedisPersistenceDefault;

import java.io.IOException;
import java.time.LocalDateTime;
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
@WebServlet( name="TimelineServlet", urlPatterns = "/timeline")
public class TimelineServlet extends HttpServlet {

    private RedisPersistence persistenceBean = new RedisPersistenceDefault();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String text = request.getParameter("text");
        User u = (User) request.getSession().getAttribute("user");

        addPost(u, text);


        response.sendRedirect("timeline?uid=" + u.getId() + "&show=all");

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uidS = request.getParameter("uid");
        String postsS = request.getParameter("show");

        int start;

        try {
            start = Integer.parseInt(request.getParameter("start"));
        } catch (NumberFormatException e){
            start = 0;
        }

        try{
            // USER
            Long uid = Long.parseLong(uidS);
            request.setAttribute("targetUser", persistenceBean.findUserById(uid));

            User current = ((User)(request.getSession().getAttribute("user")));
            List<Post> posts;

            Long postsCount;
            Long followersCount;
            Long followingsCount;

            if(uid == current.getId()){                                             // MY TIMELINE
                request.setAttribute("isMe", Boolean.TRUE);

                if(postsS != null && postsS.equals("me")){
                    posts = persistenceBean.getUserPosts(uid,start,20);
                }
                else{
                    posts = persistenceBean.getWallPosts(uid,start,20);
                }

                followersCount = persistenceBean.getFollowersCount(current.getId());
                followingsCount = persistenceBean.getFollowingsCount(current.getId());
                postsCount = persistenceBean.getPostsCount(current.getId());
            }
            else
            {
                if(uid != -1){
                    posts = persistenceBean.getWallPosts(uid,start,20);             // SOMEONE ELSE'S TIMELINE
                    request.setAttribute("explore", Boolean.FALSE);

                    followersCount = persistenceBean.getFollowersCount(uid);
                    followingsCount = persistenceBean.getFollowingsCount(uid);
                    postsCount = persistenceBean.getPostsCount(uid);
                }else{
                    posts = persistenceBean.getGlobalPosts(start, 20);              // EXPLORE
                    request.setAttribute("targetUser", current);
                    request.setAttribute("explore", Boolean.TRUE);

                    followersCount = persistenceBean.getFollowersCount(current.getId());
                    followingsCount = persistenceBean.getFollowingsCount(current.getId());
                    postsCount = persistenceBean.getPostsCount(current.getId());
                }
                request.setAttribute("isMe", Boolean.FALSE);
                request.setAttribute("isFollowed", isFollowed(current, uid));

            }

            // POSTS
            request.setAttribute("postsToShow", posts);

            // STATS
            request.setAttribute("postsCount", postsCount);
            request.setAttribute("followersCount", followersCount);
            request.setAttribute("followingsCount", followingsCount);

            RequestDispatcher rq = request.getRequestDispatcher("/WEB-INF/timeline.jsp");
            rq.forward(request, response);

        } catch (NumberFormatException e){
            e.printStackTrace();

            RequestDispatcher rq = request.getRequestDispatcher("/WEB-INF/timeline.jsp");
            rq.forward(request, response);
        }

    }


    /**
     * @param user current user
     * @param uid whose profile is viewed
     * @return true is the current user is following the currently viewed user
     */
    private boolean isFollowed(User user, Long uid){
        if(user.getId()==uid){
            return false;
        }

        List<User> following = persistenceBean.getFollowing(user.getId());

        for(User u : following){
            if(u.getId() == uid){
                return true;
            }
        }
        return false;
    }

    private void addPost(User u, String text){
        Post p = new Post();
        p.setCreated(LocalDateTime.now());
        p.setText(text);
        p.setOwnerId(u.getId());
        persistenceBean.addPost(p);
    }

}