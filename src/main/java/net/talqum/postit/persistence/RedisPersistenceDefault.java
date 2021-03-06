package net.talqum.postit.persistence;

import net.talqum.postit.domain.Post;
import net.talqum.postit.domain.User;
import net.talqum.postit.util.RedisConstants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Created by Imre on 2014.03.22.
 */
public class RedisPersistenceDefault implements RedisPersistence {

    private static JedisPool pool;

    static{
        pool = new JedisPool("192.168.0.15", 6379);
    }

    public RedisPersistenceDefault() {}

    @Override
    public boolean addUser(User u) {
        try(Jedis jedis = pool.getResource()) {
            String id = jedis.get(RedisConstants.USERNAME + ":" + u.getName() + ":" + RedisConstants.ID);
            if((id == null) || (id.equals(""))){
                Long nextUid = jedis.incr(RedisConstants.GLOBAL_NEXT_UID);

                Transaction tr = jedis.multi();
                tr.set(RedisConstants.USERNAME + ":" + u.getName() + ":" + RedisConstants.ID, nextUid.toString());
                tr.set(RedisConstants.UID + ":" + nextUid + ":" + RedisConstants.USERNAME, u.getName());
                tr.set(RedisConstants.UID + ":" + nextUid + ":" + RedisConstants.PASSWORD, u.getPassword());
                tr.set(RedisConstants.UID + ":" + nextUid + ":" + RedisConstants.TIMEOFREGISTRATION,
                          u.getTimeOfRegistration().toString());
                tr.set(RedisConstants.UID + ":" + nextUid + ":" + RedisConstants.PICTURE, u.getPicture());
                tr.sadd(RedisConstants.GLOBAL_USERS, nextUid.toString());
                tr.exec();

                u.setId(nextUid);

                return true;
            }
            else
            {
                return false;
            }
        } catch (JedisConnectionException e) {
            return false;
        }
    }

    @Override
    public User findUserById(Long uid) {

        try(Jedis jedis = pool.getResource()) {
            return getAndParseUserDataFromRedis(uid, jedis);

        } catch (JedisConnectionException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User findUserByName(String name) {
        try(Jedis jedis = pool.getResource()) {
            Long uid = Long.parseLong(jedis.get(RedisConstants.USERNAME + ":" + name + ":" + RedisConstants.ID));

            return getAndParseUserDataFromRedis(uid, jedis);
        } catch (JedisConnectionException e){
            return null;
        }

    }

    private User getAndParseUserDataFromRedis(Long uid, Jedis jedis){
        User u = new User();
        u.setId(uid);
        u.setName(jedis.get(RedisConstants.UID + ":" + uid + ":" + RedisConstants.USERNAME));
        if(u.getName() == null || u.getName().equals("")){
            return null;
        }
        u.setPassword(jedis.get(RedisConstants.UID + ":" + uid + ":" + RedisConstants.PASSWORD));
        u.setTimeOfRegistration(LocalDateTime.parse(jedis.get(RedisConstants.UID + ":" + uid + ":" + RedisConstants.TIMEOFREGISTRATION)));
        u.setPicture(jedis.get(RedisConstants.UID + ":" + uid + ":" + RedisConstants.PICTURE));

        return u;
    }

    @Override
    public boolean addPost(Post p) {
        try(Jedis jedis = pool.getResource()) {
            Long nextPid = jedis.incr(RedisConstants.GLOBAL_NEXT_PID);

            Set<String> followersIds = jedis.smembers(RedisConstants.UID + ":" + p.getOwnerId() + ":" + RedisConstants.FOLLOWERS);

            Transaction tr = jedis.multi();
            tr.set(RedisConstants.POST + ":" + nextPid, p.toString());
            for(String followerId: followersIds){
                tr.lpush(RedisConstants.UID + ":" + followerId + ":" + RedisConstants.POSTS, nextPid.toString());
            }
            tr.lpush(RedisConstants.UID + ":" + p.getOwnerId() + ":" + RedisConstants.POSTS, nextPid.toString());
            tr.lpush(RedisConstants.UID + ":" + p.getOwnerId() + ":" + RedisConstants.USERONLYPOST, nextPid.toString());
            tr.lpush(RedisConstants.GLOBAL_TIMELINE, nextPid.toString());
            tr.ltrim(RedisConstants.GLOBAL_TIMELINE, 0, 1000);
            tr.exec();

            return true;
        } catch (JedisConnectionException e) {
            return false;
        }
    }

    @Override
    public Post findPostById(Long pid) {
        try(Jedis jedis = pool.getResource()) {
            String postString = jedis.get(RedisConstants.POST + ":" + pid);

            Post p = Post.buildFromString(postString, pid);

            p.setOwner(findUserById(p.getOwnerId()));

            return p;
        } catch (JedisConnectionException e) {
            return null;
        }
    }

    @Override
    public User authUser(String name, String pass){
        User u = findUserByName(name);

        if (u != null) {
            if(u.getPassword().equals(pass)){
                return u;
            }
        }

        return null;
    }

    @Override
    public void addFollowing(User u, Long uidFollowing){
        try(Jedis jedis = pool.getResource()) {
            Transaction tr = jedis.multi();
            tr.sadd(RedisConstants.UID + ":" + uidFollowing + ":" + RedisConstants.FOLLOWERS, u.getId().toString());
            tr.sadd(RedisConstants.UID + ":" + u.getId().toString() + ":" + RedisConstants.FOLLOWING, uidFollowing.toString());
            tr.exec();
        }
    }

    @Override
    public void removeFollowing(User u, Long uidFollowing){
        try(Jedis jedis = pool.getResource()) {
            Transaction tr = jedis.multi();
            tr.srem(RedisConstants.UID + ":" + uidFollowing + ":" + RedisConstants.FOLLOWERS, u.getId().toString());
            tr.srem(RedisConstants.UID + ":" + u.getId().toString() + ":" + RedisConstants.FOLLOWING,
                       uidFollowing.toString());
            tr.exec();
        }
    }

    @Override
    public List<Post> getWallPosts(Long userID, int start, int count) {
        try(Jedis jedis = pool.getResource()) {
            List<String> postIDs = jedis.lrange(RedisConstants.UID + ":" + userID + ":" + RedisConstants.POSTS, start, start + count);

            long[] postIDsL = postIDs.stream().mapToLong(Long::parseLong).toArray();

            List<Post> posts = new ArrayList<>();

            for(long id : postIDsL){
                Post p = findPostById(id);
                if (p != null) {
                    posts.add(p);
                }
            }

            return posts;
        } catch (JedisConnectionException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Post> getUserPosts(Long userID, int start, int count) {
        try(Jedis jedis = pool.getResource()) {
            List<String> postIDs = jedis.lrange(RedisConstants.UID + ":" + userID + ":" + RedisConstants.USERONLYPOST, start, start + count);

            long[] postIDsL = postIDs.stream().mapToLong(Long::parseLong).toArray();

            List<Post> posts = new ArrayList<>();

            for(long id : postIDsL){
                Post p = findPostById(id);
                if (p != null) {
                    posts.add(p);
                }
            }

            return posts;
        } catch (JedisConnectionException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Post> getGlobalPosts(int start, int count) {
        try(Jedis jedis = pool.getResource()) {
            List<String> postIDs = jedis.lrange(RedisConstants.GLOBAL_TIMELINE, start, start + count);

            long[] postIDsL = postIDs.stream().mapToLong(Long::parseLong).toArray();

            List<Post> posts = new ArrayList<>();

            for(long id : postIDsL){
                Post p = findPostById(id);
                if (p != null) {
                    posts.add(p);
                }
            }

            return posts;
        } catch (JedisConnectionException e) {
           return new ArrayList<>();
        } 
    }

    @Override
    public List<User> getFollowers(Long uid) {
        try(Jedis jedis = pool.getResource()) {
            Set<String> followerIDs = jedis.smembers(RedisConstants.UID + ":" + uid + ":" + RedisConstants.FOLLOWERS);

            long[] followerIDsL = followerIDs.stream().mapToLong(Long::parseLong).toArray();

            List<User> followers = new ArrayList<>();

            for(long id : followerIDsL){
                User p = findUserById(id);
                if (p != null) {
                    followers.add(p);
                }
            }

            return followers;
        } catch (JedisConnectionException e) {
            return new ArrayList<>();
        } 
    }

    @Override
    public List<User> getFollowing(Long uid) {
        try(Jedis jedis = pool.getResource()) {
            Set<String> followingIDs = jedis.smembers(RedisConstants.UID + ":" + uid + ":" + RedisConstants.FOLLOWING);

            long[] followingIDsL = followingIDs.stream().mapToLong(Long::parseLong).toArray();

            List<User> following = new ArrayList<>();

            for(long id : followingIDsL){
                User p = findUserById(id);
                if (p != null) {
                    following.add(p);
                }
            }

            return following;
        } catch (JedisConnectionException e) {
            return new ArrayList<>();
        } 
    }

    @Override
    public List<User> getlatestUsers() {
        try(Jedis jedis = pool.getResource()) {
            SortingParams sp = new SortingParams();
            sp.get(RedisConstants.UID + ":*:" + RedisConstants.USERNAME);
            sp.desc();
            sp.limit(0, 10);
            List<String> userIDs = jedis.sort(RedisConstants.GLOBAL_USERS, sp);
            List<User> users = new ArrayList<>();

            for(String uid : userIDs){
                User u = findUserById(Long.parseLong(uid));
                if (u != null) {
                    users.add(u);
                }
            }

            return users;
        } catch (JedisConnectionException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Long getUserCount() {
        try(Jedis jedis = pool.getResource()) {
            return jedis.scard(RedisConstants.GLOBAL_USERS);

        } catch (JedisConnectionException e) {
           return 0L;
        }
    }

    @Override
    public Long getFollowersCount(Long uid) {
        try(Jedis jedis = pool.getResource()) {
            return jedis.scard(RedisConstants.UID + ":" + uid + ":" + RedisConstants.FOLLOWERS);
        } catch (JedisConnectionException e) {
            return 0L;
        }
    }

    @Override
    public Long getFollowingsCount(Long uid) {
        try(Jedis jedis = pool.getResource()) {
            return jedis.scard(RedisConstants.UID + ":" + uid + ":" + RedisConstants.FOLLOWING);

        } catch (JedisConnectionException e) {
            return 0L;
        }
    }

    @Override
    public Long getPostsCount(Long uid) {
        try(Jedis jedis = pool.getResource()) {
            return jedis.llen(RedisConstants.UID + ":" + uid + ":" + RedisConstants.USERONLYPOST);

        } catch (JedisConnectionException e) {
            return 0L;
        }
    }
}
