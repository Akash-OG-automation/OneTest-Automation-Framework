package com.onetest.reports;

import java.util.HashMap;
import java.util.Map;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.ExtentReports;

public class ExtentTestManager {

    static Map<String, ExtentTest> extentTestMap = new HashMap<>();
    static ExtentReports extent = ExtentManager.getInstance();

    public static ExtentTest getTest(String testName) {
        return extentTestMap.get(testName);
    }

    public static ExtentTest startTest(String testName, String description) {
        ExtentTest test = extent.createTest(testName, description);
        extentTestMap.put(testName, test);
        return test;
    }
}
