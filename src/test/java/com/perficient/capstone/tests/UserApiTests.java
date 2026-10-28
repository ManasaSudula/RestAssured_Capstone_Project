package com.perficient.capstone.tests;

import com.perficient.capstone.base.BaseTest;
import com.perficient.capstone.pojo.User;
import com.perficient.capstone.utils.ApiAssertions;
import com.perficient.capstone.utils.ApiClient;
import com.perficient.capstone.utils.Endpoints;
import com.perficient.capstone.utils.TestDataUtil;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Assignment 3 - User API automation (GET all, GET by id, POST, PUT, DELETE).
 * Tests are chained with dependsOnMethods because they share the user created in test 2.
 */
public class UserApiTests extends BaseTest {

    private int userId;
    private User createdUser;

    @Test(description = "GET /users - list users")
    public void getAllUsers() {
        Response response = ApiClient.get(Endpoints.USERS);

        ApiAssertions.assertStatusCode(response, 200);
        List<Object> ids = response.jsonPath().getList("id");
        Assert.assertFalse(ids.isEmpty(), "User list should not be empty");
        Assert.assertNotNull(response.jsonPath().getString("[0].name"), "First user should have a name");
        ApiAssertions.assertResponseTime(response);
    }

    @Test(description = "POST /users - create a new user", dependsOnMethods = "getAllUsers")
    public void createUser() {
        createdUser = TestDataUtil.randomUser();               // POJO as request body
        Response response = ApiClient.post(Endpoints.USERS, createdUser);

        ApiAssertions.assertStatusCode(response, 201);
        userId = response.jsonPath().getInt("id");
        Assert.assertTrue(userId > 0, "Created user must have an id");
        Assert.assertEquals(response.jsonPath().getString("name"), createdUser.getName());
        Assert.assertEquals(response.jsonPath().getString("email"), createdUser.getEmail());
        Assert.assertEquals(response.jsonPath().getString("status"), "active");
        ApiAssertions.assertResponseTime(response);
    }

    @Test(description = "GET /users/{id} - fetch the created user", dependsOnMethods = "createUser")
    public void getUserById() {
        Response response = ApiClient.get(Endpoints.userById(userId));

        ApiAssertions.assertStatusCode(response, 200);
        Assert.assertEquals(response.jsonPath().getInt("id"), userId);
        Assert.assertEquals(response.jsonPath().getString("name"), createdUser.getName());
        Assert.assertEquals(response.jsonPath().getString("email"), createdUser.getEmail());
        ApiAssertions.assertResponseTime(response);
    }

    @Test(description = "PUT /users/{id} - update the user", dependsOnMethods = "getUserById")
    public void updateUser() {
        User update = new User(createdUser.getName() + " Updated", createdUser.getEmail(),
                createdUser.getGender(), "inactive");
        Response response = ApiClient.put(Endpoints.userById(userId), update);

        ApiAssertions.assertStatusCode(response, 200);
        Assert.assertEquals(response.jsonPath().getString("name"), update.getName());
        Assert.assertEquals(response.jsonPath().getString("status"), "inactive");
        ApiAssertions.assertResponseTime(response);
    }

    @Test(description = "DELETE /users/{id} - delete the user and verify it is gone",
          dependsOnMethods = "updateUser")
    public void deleteUser() {
        Response response = ApiClient.delete(Endpoints.userById(userId));
        ApiAssertions.assertStatusCode(response, 204);
        ApiAssertions.assertResponseTime(response);

        // verify deletion
        ApiAssertions.assertStatusCode(ApiClient.get(Endpoints.userById(userId)), 404);
    }
}
