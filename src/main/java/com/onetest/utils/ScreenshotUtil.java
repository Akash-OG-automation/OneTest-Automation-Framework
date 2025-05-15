package com.onetest.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.onetest.base.DriverManager;

public class ScreenshotUtil {

	private static final String screenshotDir = ConfigurationManager.getValue("SCREENSHOT_DIR");

	public static String captureScreenshot(String stepName) {
		WebDriver driver;
		try {
			driver = DriverManager.getDriver();
		} catch (IllegalStateException e) {
			System.out.println("⚠️ Cannot capture screenshot: WebDriver is not initialized.");
			return null;
		}

		if (driver == null) {
			System.out.println("⚠️ Driver is null in ScreenshotUtil.");
			return null;
		}

		try {
			File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			String path = screenshotDir + stepName.replaceAll("\\s+", "_") + "_" + timestamp + ".png";
			FileUtils.copyFile(src, new File(path));
			return path;
		} catch (Exception e) {
			System.out.println("❌ Failed to capture screenshot: " + e.getMessage());
			return null;
		}
	}
}
