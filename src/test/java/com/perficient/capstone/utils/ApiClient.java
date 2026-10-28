package com.perficient.capstone.utils;

import com.perficient.capstone.config.ConfigReader;
import com.perficient.capstone.listeners.ExtentTestManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Reusable wrapper around Rest Assured. Every test uses these methods, so
 * base URL, authentication, headers and report logging live in ONE place.
 */
public final class ApiClient {

    private ApiClient() { }

    /** Builds the common request specification (base URI, JSON headers, Bearer authentication). */
    private static RequestSpecification spec() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getBaseUrl())
                .setBasePath(ConfigReader.getBasePath())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(reportLogger());

        String token = ConfigReader.getToken();
        if (!token.isEmpty()) {
            // Authentication technique: Bearer token in the Authorization header
            builder.addHeader("Authorization", "Bearer " + token);
        }
        return given().spec(builder.build());
    }

    // ---------- HTTP helpers ----------
    public static Response get(String path) {
        return spec().when().get(path);
    }

    public static Response get(String path, Map<String, ?> queryParams) {
        return spec().queryParams(queryParams).when().get(path);
    }

    public static Response post(String path, Object body) {
        return spec().body(body).when().post(path);
    }

    public static Response put(String path, Object body) {
        return spec().body(body).when().put(path);
    }

    public static Response delete(String path) {
        return spec().when().delete(path);
    }

    /** Writes each request/response into the Extent report (Authorization header is NOT logged). */
    private static Filter reportLogger() {
        return (req, res, ctx) -> {
            Response response = ctx.next(req, res);
            if (ExtentTestManager.getTest() != null) {
                String body = req.getBody() == null ? "-" : String.valueOf((Object) req.getBody());
                ExtentTestManager.getTest().info(
                        "<b>" + req.getMethod() + "</b> " + req.getURI()
                        + "<br/>Request body: <pre>" + escape(body) + "</pre>"
                        + "Status: <b>" + response.getStatusCode() + "</b> | Time: "
                        + response.getTime() + " ms"
                        + "<br/>Response: <pre>" + escape(truncate(response.asString())) + "</pre>");
            }
            return response;
        };
    }

    private static String truncate(String s) {
        return s.length() > 1500 ? s.substring(0, 1500) + " ...(truncated)" : s;
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
