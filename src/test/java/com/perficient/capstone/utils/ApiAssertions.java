package com.perficient.capstone.utils;

import com.perficient.capstone.config.ConfigReader;
import io.restassured.response.Response;
import org.testng.Assert;

/** Common validations: status code and response time. */
public final class ApiAssertions {
    private ApiAssertions() { }

    public static void assertStatusCode(Response response, int expected) {
        Assert.assertEquals(response.getStatusCode(), expected,
                "Unexpected status code. Response body: " + response.asString());
    }

    public static void assertResponseTime(Response response) {
        long max = ConfigReader.getLong("maxResponseTimeMs", 5000);
        Assert.assertTrue(response.getTime() <= max,
                "Response time " + response.getTime() + " ms exceeded limit of " + max + " ms");
    }
}
