package net.talqum.postit.restapi;

import net.talqum.postit.domain.Post;
import net.talqum.postit.persistence.RedisPersistence;
import net.talqum.postit.persistence.RedisPersistenceDefault;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Created by Imre on 2014.04.28..
 */
@Path("/posts")
public class PostController {

    private RedisPersistence persistenceBean = new RedisPersistenceDefault();

    @Path("/all")
    @GET
    @Produces("application/json")
    public JsonArray getAll(@DefaultValue("0") @QueryParam("from")int from, @DefaultValue("20") @QueryParam("count")int count){
        List<Post> posts = persistenceBean.getGlobalPosts(from, count);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (Post post : posts) {
            builder.add(Json.createObjectBuilder().add("owner", post.getOwner().getName()).add("text", post.getText()));
        }

        return builder.build();
    }

    @Path("/user")
    @GET
    @Produces("application/json")
    public JsonArray getAll(@QueryParam("uid")Long uid, @DefaultValue("0") @QueryParam("from")int from, @DefaultValue("20") @QueryParam("count")int count){
        List<Post> posts = persistenceBean.getUserPosts(uid, from, count);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (Post post : posts) {
            builder.add(Json.createObjectBuilder().add("owner", post.getOwner().getName()).add("text", post.getText()));
        }

        return builder.build();
    }

}
