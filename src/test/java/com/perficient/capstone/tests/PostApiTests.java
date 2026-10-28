package com.perficient.capstone.tests;

import com.perficient.capstone.base.BaseTest;
import com.perficient.capstone.utils.ApiAssertions;
import com.perficient.capstone.utils.ApiClient;
import com.perficient.capstone.utils.Endpoints;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * Assignment 4 - "Product" API automation.
 * GoRest has no /products endpoint; its resource API is /posts (see the API list in the
 * project guide), so "Product" maps to "Post": ID = post id, Name = post title.
 */
public class PostApiTests extends BaseTest {

    private int postId;
    private String postTitle;

    @Test(description = "GET /posts - list posts")
    public void getAllPosts() {
        Response response = ApiClient.get(Endpoints.POSTS);

        ApiAssertions.assertStatusCode(response, 200);
        List<Integer> ids = response.jsonPath().getList("id");
        Assert.assertFalse(ids.isEmpty(), "Post list should not be empty");
        postId = ids.get(0);
        postTitle = response.jsonPath().getString("[0].title");
        ApiAssertions.assertResponseTime(response);
    }

    @Test(description = "GET /posts/{id} - post details (ID and title)", dependsOnMethods = "getAllPosts")
    public void getPostDetails() {
        Response response = ApiClient.get(Endpoints.postById(postId));

        ApiAssertions.assertStatusCode(response, 200);
        Assert.assertEquals(response.jsonPath().getInt("id"), postId);
        Assert.assertEquals(response.jsonPath().getString("title"), postTitle);
        ApiAssertions.assertResponseTime(response);
    }

    @Test(description = "GET /posts?title=... - search posts by title", dependsOnMethods = "getAllPosts")
    public void searchPost() {
        String keyword = postTitle.split("\\s+")[0];
        Response response = ApiClient.get(Endpoints.POSTS, Map.of("title", keyword));

        ApiAssertions.assertStatusCode(response, 200);
        List<String> titles = response.jsonPath().getList("title");
        Assert.assertFalse(titles.isEmpty(), "Search should return at least one post for: " + keyword);
        for (String t : titles) {
            Assert.assertTrue(t.toLowerCase().contains(keyword.toLowerCase()),
                    "Title '" + t + "' does not contain '" + keyword + "'");
        }
        ApiAssertions.assertResponseTime(response);
    }

    @Test(description = "GET /posts/{id}/comments - comments on a post", dependsOnMethods = "getAllPosts")
    public void getCommentsOnPost() {
        Response response = ApiClient.get(Endpoints.commentsOfPost(postId));

        ApiAssertions.assertStatusCode(response, 200);
        List<Integer> postIds = response.jsonPath().getList("post_id");
        for (Integer pid : postIds) {
            Assert.assertEquals(pid.intValue(), postId, "Comment belongs to a different post");
        }
        ApiAssertions.assertResponseTime(response);
    }
}
