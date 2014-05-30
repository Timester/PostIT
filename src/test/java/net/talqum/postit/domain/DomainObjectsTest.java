package net.talqum.postit.domain;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

/**
 * Created by Imre on 2014.03.24..
 */
public class DomainObjectsTest {

    @Test
    public void testPostToString() throws Exception {
        Post p = new Post();
        p.setId(1L);
        p.setOwnerId(2L);
        p.setText("Test post.");
        LocalDateTime time = LocalDateTime.now();
        p.setCreated(time);

        assertEquals("2#" + time + "#Test post.", p.toString());
    }

    @Test
    public void testPostBuildFromString() throws Exception {
        Post p = new Post();
        p.setId(1L);
        p.setOwnerId(2L);
        p.setText("Test post.");
        LocalDateTime time = LocalDateTime.now();
        p.setCreated(time);

        Post other = Post.buildFromString(p.toString(), 1L);

        assertEquals(p.getId(), other.getId());
        assertEquals(p.getCreated(), other.getCreated());
        assertEquals(p.getText(), other.getText());
        assertEquals(p.getOwnerId(), other.getOwnerId());
    }
}
