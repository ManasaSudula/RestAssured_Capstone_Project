package com.perficient.capstone.utils;

import com.perficient.capstone.pojo.User;

import java.util.UUID;

/** Test data management: generates unique data so tests never clash (GoRest requires unique emails). */
public final class TestDataUtil {
    private TestDataUtil() { }

    public static String uniqueEmail() {
        return "qa_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    public static User randomUser() {
        return new User("QA Tester " + UUID.randomUUID().toString().substring(0, 5),
                uniqueEmail(), "male", "active");
    }
}
