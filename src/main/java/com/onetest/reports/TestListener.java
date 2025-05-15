package com.onetest.reports;

import com.aventstack.extentreports.Status;
import com.onetest.utils.ScreenshotUtil;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        // Start of test suite
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.getInstance().flush();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        ExtentTestManager.startTest(testName, description != null ? description : "");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTestManager.getTest(result.getMethod().getMethodName()).log(Status.PASS, "✅ Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTestManager.getTest(result.getMethod().getMethodName()).log(Status.FAIL, "❌ Test failed: " + result.getThrowable());
        String screenshotPath = ScreenshotUtil.captureScreenshot(result.getMethod().getMethodName());
        ExtentTestManager.getTest(result.getMethod().getMethodName())
                .addScreenCaptureFromPath(screenshotPath);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTestManager.getTest(result.getMethod().getMethodName()).log(Status.SKIP, "⚠️ Test skipped");
    }
}
