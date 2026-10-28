package com.perficient.capstone.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/** TestNG listener that feeds pass / fail / skip results into the Extent HTML report. */
public class ExtentTestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTestManager.startTest(result.getMethod().getMethodName(),
                result.getMethod().getDescription());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTestManager.getTest().pass("Test PASSED");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTestManager.getTest().fail(result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTestManager.startTest(result.getMethod().getMethodName(),
                result.getMethod().getDescription());
        ExtentTestManager.getTest().skip(
                result.getThrowable() != null ? result.getThrowable().getMessage() : "Skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentTestManager.getReports().flush();
    }
}
