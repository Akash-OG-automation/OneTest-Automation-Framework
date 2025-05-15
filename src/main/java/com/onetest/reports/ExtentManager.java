package com.onetest.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

    private static ExtentReports extent;
    private static final String REPORT_PATH = "TestReport.html";

    public static ExtentReports getInstance() {
        if (extent == null) {
            extent = new ExtentReports();
            extent.attachReporter(new ExtentSparkReporter(REPORT_PATH));
        }
        return extent;
    }
}
