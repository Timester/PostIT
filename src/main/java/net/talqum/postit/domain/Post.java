package net.talqum.postit.domain;

import java.time.LocalDateTime;

/**
 * Created by Imre on 2014.03.17..
 */
public class Post {

    private Long id;

    private String text;

    private LocalDateTime created;

    private Long ownerId;

    private User owner;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString(){
        return ownerId + "#" + created + "#" + text;
    }

    public static Post buildFromString(String fromRedis, Long pid){
        Post toReturn = new Post();
        String[] tokenized = fromRedis.split("#");

        toReturn.setOwnerId(Long.parseLong(tokenized[0]));
        toReturn.setCreated(LocalDateTime.parse(tokenized[1]));
        toReturn.setText(tokenized[2]);
        toReturn.setId(pid);

        return toReturn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Post post = (Post) o;

        if (created != null ? !created.equals(post.created) : post.created != null) {
            return false;
        }
        if (id != null ? !id.equals(post.id) : post.id != null) {
            return false;
        }
        if (ownerId != null ? !ownerId.equals(post.ownerId) : post.ownerId != null) {
            return false;
        }
        if (text != null ? !text.equals(post.text) : post.text != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (ownerId != null ? ownerId.hashCode() : 0);
        return result;
    }
}
