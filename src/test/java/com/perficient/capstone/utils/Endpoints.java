package com.perficient.capstone.utils;

/** Central place for all API paths (relative to basePath /public/v2). */
public final class Endpoints {
    private Endpoints() { }

    public static final String USERS = "/users";
    public static final String POSTS = "/posts";
    public static final String COMMENTS = "/comments";

    public static String userById(int id)        { return USERS + "/" + id; }
    public static String postById(int id)        { return POSTS + "/" + id; }
    public static String postsOfUser(int userId) { return USERS + "/" + userId + "/posts"; }
    public static String commentsOfPost(int id)  { return POSTS + "/" + id + "/comments"; }
}
