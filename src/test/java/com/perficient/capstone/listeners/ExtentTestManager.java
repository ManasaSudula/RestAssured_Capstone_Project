package com.perficient.capstone.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.perficient.capstone.config.ConfigReader;

/** Creates the single ExtentReports instance and keeps the current test per thread. */
public final class ExtentTestManager {

    public static final String REPORT_PATH = "target/extent-reports/ExecutionReport.html";

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> CURRENT = new ThreadLocal<>();

    private ExtentTestManager() { }

    public static synchronized ExtentReports getReports() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);
            spark.config().setDocumentTitle("GoRest API Automation Report");
            spark.config().setReportName("Rest Assured Capstone - Execution Report");
            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Environment", ConfigReader.getEnv().toUpperCase());
            extent.setSystemInfo("Base URL", ConfigReader.getBaseUrl());
            extent.setSystemInfo("Java", System.getProperty("java.version"));
            extent.setSystemInfo("OS", System.getProperty("os.name"));
        }
        return extent;
    }

    public static ExtentTest startTest(String name, String description) {
        ExtentTest test = getReports().createTest(name, description);
        CURRENT.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return CURRENT.get();
    }
}
