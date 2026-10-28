package com.perficient.capstone.base;

import com.perficient.capstone.config.ConfigReader;
import org.testng.annotations.BeforeSuite;

/** Parent of all test classes: one-time setup. */
public class BaseTest {

    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        System.out.println("Running on environment : " + ConfigReader.getEnv().toUpperCase());
        System.out.println("Base URL               : " + ConfigReader.getBaseUrl() + ConfigReader.getBasePath());
        if (ConfigReader.getToken().isEmpty()) {
            System.out.println("WARNING: no token set - POST/PUT/DELETE will return 401. "
                    + "Use -Dtoken=... or GOREST_TOKEN.");
        }
    }
}
