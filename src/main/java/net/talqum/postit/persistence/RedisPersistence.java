package net.talqum.postit.persistence;

import net.talqum.postit.domain.Post;
import net.talqum.postit.domain.User;

import java.util.List;

/**
 * Created by Imre on 2014.04.13..
 */
public interface RedisPersistence {

    public boolean addUser(User u);

    public User findUserById(Long uid);

    public User findUserByName(String name);

    public boolean addPost(Post p);

    public Post findPostById(Long pid);

    /**
     * Checks if a user with the given name exists, and whether the password is correct.
     * @param name username.
     * @param pass password.
     * @return the User object of the authenticated user or null.
     */
    public User authUser(String name, String pass);

    /**
     * Adds a new User to the current user's following set.
     * @param u user that follows someone.
     * @param uidFollowing the other user who is being followed.
     */
    public void addFollowing(User u, Long uidFollowing);

    /**
     * Removes a user from the current users following set.
     * @param u the user who unfollows someone.
     * @param uidFollowing the other user who has been unfollowed.
     */
    public void removeFollowing(User u, Long uidFollowing);

    /**
     * Returns the users following the one given in the parameter.
     * @param uid the user whose followers we want to get.
     * @return a List of following users
     */
    public List<User> getFollowers(Long uid);

    /**
     * Returns the users followed by the one given in the parameter
     * @param uid user id
     * @return a List of the followed users
     */
    public List<User> getFollowing(Long uid);

    /**
     * Get posts for a specific user's wall. The posts of the user and the posts of who he/she follows.
     * @param userID user id of the current user.
     * @param start start idx of the retrieved posts.
     * @param count how many posts should be retrieved.
     * @return a list of posts.
     */
    public List<Post> getWallPosts(Long userID, int start, int count);

    /**
     * Get posts by a specific user only.
     * @param userID user id of the current user.
     * @param start start idx of the retrieved posts.
     * @param count how many posts should be retrieved.
     * @return a list of posts.
     */
    public List<Post> getUserPosts(Long userID, int start, int count);

    /**
     * Get global posts.
     * @param start start idx of the retrieved posts.
     * @param count how many posts should be retrieved.
     * @return a list of posts.
     */
    public List<Post> getGlobalPosts(int start, int count);

    /**
     * Returns the 10 latest registered users
     * @return a list of users.
     */
    public List<User> getlatestUsers();

    public Long getUserCount();

    public Long getFollowersCount(Long uid);

    public Long getFollowingsCount(Long uid);

    public Long getPostsCount(Long uid);

}
