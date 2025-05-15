package com.onetest.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DriverManager {

	private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static final int DEFAULT_IMPLICIT_WAIT = 10; // in seconds

	public static WebDriver initDriver(String browserName) {
		if (driver.get() == null) {
			switch (browserName.toLowerCase()) {
			case "chrome":
				WebDriverManager.chromedriver().setup();
				driver.set(new ChromeDriver());
				break;
			case "firefox":
				WebDriverManager.firefoxdriver().setup();
				driver.set(new FirefoxDriver());
				break;
			case "edge":
				WebDriverManager.edgedriver().setup();
				driver.set(new EdgeDriver());
				break;
			default:
				throw new UnsupportedOperationException("Unsupported browser: " + browserName);
			}
			getDriver().manage().window().maximize();
			getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(DEFAULT_IMPLICIT_WAIT));
			System.out.println("🌐 [DriverManager] Initialized " + browserName + " driver.");
		}
		return getDriver();
	}

	public static WebDriver getDriver() {
		WebDriver currentDriver = driver.get();
		if (currentDriver == null) {
			throw new IllegalStateException("Driver is not initialized. Call initDriver() first.");
		}
		return currentDriver;
	}

	public static WebDriverWait getWait(int timeoutInSeconds) {
		return new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutInSeconds));
	}

	public static WebDriverWait getWait() {
		return new WebDriverWait(getDriver(), Duration.ofSeconds(DEFAULT_IMPLICIT_WAIT));
	}

	public static void quitDriver() {
		WebDriver currentDriver = driver.get();
		if (currentDriver != null) {
			currentDriver.quit();
			driver.remove();
			System.out.println("🛑 [DriverManager] Browser closed.");
		}
	}
}