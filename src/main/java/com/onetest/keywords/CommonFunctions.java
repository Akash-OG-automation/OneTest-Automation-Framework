package com.onetest.keywords;

import com.onetest.base.DriverManager;
import com.onetest.utils.LocatorReader;
import com.onetest.utils.LocatorReader.Locator;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.onetest.utils.ConfigurationManager;


import java.time.Duration;
import java.util.List;
import java.util.Set;

public class CommonFunctions {

	private static final int DEFAULT_TIMEOUT = Integer.parseInt(ConfigurationManager.getValue("DEFAULT_TIMEOUT")); // in seconds

	// --- Browser Navigation ---

	public static void setBrowser(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [setBrowser] Browser name cannot be null or empty.");
			throw new IllegalArgumentException("Browser name cannot be null or empty.");
		}
		String browserName = inputParams[0].trim();
		System.out.println("🌐 [setBrowser] Initializing browser: " + browserName);
		DriverManager.initDriver(browserName);
	}

	public static void setURL(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [setURL] URL cannot be null or empty.");
			throw new IllegalArgumentException("URL cannot be null or empty.");
		}
		String url = inputParams[0].trim();
		System.out.println("🌐 [setURL] Navigating to URL: " + url);
		DriverManager.getDriver().get(url);
		DriverManager.getDriver().manage().window().maximize();
	}

	public static void navigateTo(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [navigateTo] URL cannot be null or empty.");
			throw new IllegalArgumentException("URL cannot be null or empty.");
		}
		String url = inputParams[0].trim();
		System.out.println("➡️ [navigateTo] Navigating to: " + url);
		DriverManager.getDriver().navigate().to(url);
	}

	public static void navigateBack() {
		System.out.println("⬅️ [navigateBack] Navigating back.");
		DriverManager.getDriver().navigate().back();
	}

	public static void navigateForward() {
		System.out.println("➡️ [navigateForward] Navigating forward.");
		DriverManager.getDriver().navigate().forward();
	}

	public static void refreshPage() {
		System.out.println("🔄 [refreshPage] Refreshing the page.");
		DriverManager.getDriver().navigate().refresh();
	}

	// --- Element Interaction ---

	public static void setValue(String... inputParams) {
		if (inputParams == null || inputParams.length < 2 || inputParams[0] == null || inputParams[0].trim().isEmpty()
				|| inputParams[1] == null) {
			System.err.println("⚠️ [setValue] Locator key and value cannot be null or empty.");
			throw new IllegalArgumentException("Locator key and value cannot be null or empty.");
		}
		String locatorKey = inputParams[0].trim();
		String value = inputParams[1];
		WebElement element = searchElement(locatorKey);
		System.out.println("⌨️ [setValue] Setting value '" + value + "' in element with locator: " + locatorKey);
		element.clear();
		element.sendKeys(value);
	}

	public static void clickElement(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [clickElement] Locator key cannot be null or empty.");
			throw new IllegalArgumentException("Locator key cannot be null or empty.");
		}
		String locatorKey = inputParams[0].trim();
		WebElement element = searchElement(locatorKey);
		System.out.println("🖱️ [clickElement] Clicking on element with locator: " + locatorKey);
		element.click();
	}

	public static String getText(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [getText] Locator key cannot be null or empty.");
			throw new IllegalArgumentException("Locator key cannot be null or empty.");
		}
		String locatorKey = inputParams[0].trim();
		WebElement element = searchElement(locatorKey);
		String text = element.getText();
		System.out.println("📄 [getText] Text from element with locator '" + locatorKey + "': " + text);
		return text;
	}

	@SuppressWarnings("deprecation")
	public static String getAttribute(String... inputParams) {
		if (inputParams == null || inputParams.length < 2 || inputParams[0] == null || inputParams[0].trim().isEmpty()
				|| inputParams[1] == null || inputParams[1].trim().isEmpty()) {
			System.err.println("⚠️ [getAttribute] Locator key and attribute name cannot be null or empty.");
			throw new IllegalArgumentException("Locator key and attribute name cannot be null or empty.");
		}
		String locatorKey = inputParams[0].trim();
		String attributeName = inputParams[1].trim();
		WebElement element = searchElement(locatorKey);
		String attributeValue = element.getAttribute(attributeName);
		System.out.println("🏷️ [getAttribute] Attribute '" + attributeName + "' value for element with locator '"
				+ locatorKey + "': " + attributeValue);
		return attributeValue;
	}

	public static boolean isDisplayed(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [isDisplayed] Locator key cannot be null or empty.");
			throw new IllegalArgumentException("Locator key cannot be null or empty.");
		}
		String locatorKey = inputParams[0].trim();
		try {
			boolean displayed = searchElement(locatorKey).isDisplayed();
			System.out
					.println("👁️ [isDisplayed] Element with locator '" + locatorKey + "' is displayed: " + displayed);
			return displayed;
		} catch (NoSuchElementException e) {
			System.out.println("⚠️ [isDisplayed] Element with locator '" + locatorKey + "' not found.");
			return false;
		}
	}

	public static boolean isEnabled(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [isEnabled] Locator key cannot be null or empty.");
			throw new IllegalArgumentException("Locator key cannot be null or empty.");
		}
		String locatorKey = inputParams[0].trim();
		boolean enabled = searchElement(locatorKey).isEnabled();
		System.out.println("✅/❌ [isEnabled] Element with locator '" + locatorKey + "' is enabled: " + enabled);
		return enabled;
	}

	public static boolean isSelected(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [isSelected] Locator key cannot be null or empty.");
			throw new IllegalArgumentException("Locator key cannot be null or empty.");
		}
		String locatorKey = inputParams[0].trim();
		boolean selected = searchElement(locatorKey).isSelected();
		System.out.println("🔘 [isSelected] Element with locator '" + locatorKey + "' is selected: " + selected);
		return selected;
	}

	public static void hoverOnElement(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [hoverOnElement] Locator key cannot be null or empty.");
			throw new IllegalArgumentException("Locator key cannot be null or empty.");
		}
		String locatorKey = inputParams[0].trim();
		WebElement element = searchElement(locatorKey);
		Actions actions = new Actions(DriverManager.getDriver());
		actions.moveToElement(element).perform();
		System.out.println("🖱️ [hoverOnElement] Hovered on element with locator: " + locatorKey);
	}

	public static void doubleClickElement(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [doubleClickElement] Locator key cannot be null or empty.");
			throw new IllegalArgumentException("Locator key cannot be null or empty.");
		}
		String locatorKey = inputParams[0].trim();
		WebElement element = searchElement(locatorKey);
		Actions actions = new Actions(DriverManager.getDriver());
		actions.doubleClick(element).perform();
		System.out.println("🖱️🖱️ [doubleClickElement] Double-clicked on element with locator: " + locatorKey);
	}

	public static void rightClickElement(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [rightClickElement] Locator key cannot be null or empty.");
			throw new IllegalArgumentException("Locator key cannot be null or empty.");
		}
		String locatorKey = inputParams[0].trim();
		WebElement element = searchElement(locatorKey);
		Actions actions = new Actions(DriverManager.getDriver());
		actions.contextClick(element).perform();
		System.out.println("🖱️ [rightClickElement] Right-clicked on element with locator: " + locatorKey);
	}

	// --- Dropdown Handling ---

	public static void selectFromDropdownByVisibleText(String... inputParams) {
		if (inputParams == null || inputParams.length < 2 || inputParams[0] == null || inputParams[0].trim().isEmpty()
				|| inputParams[1] == null || inputParams[1].trim().isEmpty()) {
			System.err.println(
					"⚠️ [selectFromDropdownByVisibleText] Locator key and visible text cannot be null or empty.");
			throw new IllegalArgumentException("Locator key and visible text cannot be null or empty.");
		}
		String locatorKey = inputParams[0].trim();
		String visibleText = inputParams[1].trim();
		WebElement dropdownElement = searchElement(locatorKey);
		Select select = new Select(dropdownElement);
		System.out.println("🔽 [selectFromDropdownByVisibleText] Selecting option with text '" + visibleText
				+ "' from dropdown with locator: " + locatorKey);
		select.selectByVisibleText(visibleText);
	}

	public static void selectFromDropdownByValue(String... inputParams) {
		if (inputParams == null || inputParams.length < 2 || inputParams[0] == null || inputParams[0].trim().isEmpty()
				|| inputParams[1] == null || inputParams[1].trim().isEmpty()) {
			System.err.println("⚠️ [selectFromDropdownByValue] Locator key and value cannot be null or empty.");
			throw new IllegalArgumentException("Locator key and value cannot be null or empty.");
		}
		String locatorKey = inputParams[0].trim();
		String value = inputParams[1].trim();
		WebElement dropdownElement = searchElement(locatorKey);
		Select select = new Select(dropdownElement);
		System.out.println("🔽 [selectFromDropdownByValue] Selecting option with value '" + value
				+ "' from dropdown with locator: " + locatorKey);
		select.selectByValue(value);
	}

	public static void selectFromDropdownByIndex(String... inputParams) {
		if (inputParams == null || inputParams.length < 2 || inputParams[0] == null || inputParams[0].trim().isEmpty()
				|| inputParams[1] == null || inputParams[1].trim().isEmpty()) {
			System.err.println("⚠️ [selectFromDropdownByIndex] Locator key and index cannot be null or empty.");
			throw new IllegalArgumentException("Locator key and index cannot be null or empty.");
		}
		String locatorKey = inputParams[0].trim();
		int index = Integer.parseInt(inputParams[1].trim());
		WebElement dropdownElement = searchElement(locatorKey);
		Select select = new Select(dropdownElement);
		System.out.println("🔽 [selectFromDropdownByIndex] Selecting option at index '" + index
				+ "' from dropdown with locator: " + locatorKey);
		select.selectByIndex(index);
	}

	public static List<WebElement> getAllDropdownOptions(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [getAllDropdownOptions] Locator key cannot be null or empty.");
			throw new IllegalArgumentException("Locator key cannot be null or empty.");
		}
		String locatorKey = inputParams[0].trim();
		WebElement dropdownElement = searchElement(locatorKey);
		Select select = new Select(dropdownElement);
		List<WebElement> options = select.getOptions();
		System.out.println("📄 [getAllDropdownOptions] Retrieved " + options.size()
				+ " options from dropdown with locator: " + locatorKey);
		return options;
	}

	// --- Alert Handling ---

	public static void acceptAlert() {
		Alert alert = DriverManager.getDriver().switchTo().alert();
		System.out.println("🔔 [acceptAlert] Accepting alert with message: " + alert.getText());
		alert.accept();
	}

	public static void dismissAlert() {
		Alert alert = DriverManager.getDriver().switchTo().alert();
		System.out.println("🔔 [dismissAlert] Dismissing alert with message: " + alert.getText());
		alert.dismiss();
	}

	public static String getAlertText() {
		Alert alert = DriverManager.getDriver().switchTo().alert();
		String alertText = alert.getText();
		System.out.println("📄 [getAlertText] Text from alert: " + alertText);
		return alertText;
	}

	public static void setAlertText(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null) {
			System.err.println("⚠️ [setAlertText] Text to set in alert cannot be null.");
			throw new IllegalArgumentException("Text to set in alert cannot be null.");
		}
		String text = inputParams[0];
		Alert alert = DriverManager.getDriver().switchTo().alert();
		System.out.println("⌨️ [setAlertText] Setting text '" + text + "' in alert.");
		alert.sendKeys(text);
	}

	// --- Frame Handling ---

	public static void switchToFrameByIndex(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [switchToFrameByIndex] Frame index cannot be null or empty.");
			throw new IllegalArgumentException("Frame index cannot be null or empty.");
		}
		int index = Integer.parseInt(inputParams[0].trim());
		DriverManager.getDriver().switchTo().frame(index);
		System.out.println("🖼️ [switchToFrameByIndex] Switched to frame with index: " + index);
	}

	public static void switchToFrameByNameOrId(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [switchToFrameByNameOrId] Frame name or ID cannot be null or empty.");
			throw new IllegalArgumentException("Frame name or ID cannot be null or empty.");
		}
		String nameOrId = inputParams[0].trim();
		DriverManager.getDriver().switchTo().frame(nameOrId);
		System.out.println("🖼️ [switchToFrameByNameOrId] Switched to frame with name or ID: " + nameOrId);
	}

	public static void switchToFrameByWebElement(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [switchToFrameByWebElement] Locator key for frame cannot be null or empty.");
			throw new IllegalArgumentException("Locator key for frame cannot be null or empty.");
		}
		String locatorKey = inputParams[0].trim();
		WebElement frameElement = searchElement(locatorKey);
		DriverManager.getDriver().switchTo().frame(frameElement);
		System.out.println(
				"🖼️ [switchToFrameByWebElement] Switched to frame using WebElement with locator: " + locatorKey);
	}

	public static void switchToDefaultContent() {
		DriverManager.getDriver().switchTo().defaultContent();
		System.out.println("🖼️ [switchToDefaultContent] Switched back to default content.");
	}

	public static void switchToParentFrame() {
		DriverManager.getDriver().switchTo().parentFrame();
		System.out.println("🖼️ [switchToParentFrame] Switched to parent frame.");
	}

	// --- Window Handling ---

	public static void switchToWindowByTitle(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [switchToWindowByTitle] Window title cannot be null or empty.");
			throw new IllegalArgumentException("Window title cannot be null or empty.");
		}
		String targetTitle = inputParams[0].trim();
		String currentWindowHandle = DriverManager.getDriver().getWindowHandle();
		Set<String> windowHandles = DriverManager.getDriver().getWindowHandles();

		for (String handle : windowHandles) {
			if (!handle.equals(currentWindowHandle)) {
				DriverManager.getDriver().switchTo().window(handle);
				if (DriverManager.getDriver().getTitle().contains(targetTitle)) {
					System.out.println(
							"⚠️ [switchToWindowByTitle] Switched to window with title containing: " + targetTitle);
					return;
				}
			}
		}
		System.err.println("⚠️ [switchToWindowByTitle] Window with title containing '" + targetTitle + "' not found.");
		throw new NoSuchWindowException("Window with title containing '" + targetTitle + "' not found.");
	}

	public static void switchToNewWindow() {
		String currentWindowHandle = DriverManager.getDriver().getWindowHandle();
		Set<String> windowHandles = DriverManager.getDriver().getWindowHandles();
		for (String handle : windowHandles) {
			if (!handle.equals(currentWindowHandle)) {
				DriverManager.getDriver().switchTo().window(handle);
				System.out.println("🖼️ [switchToNewWindow] Switched to new window with title: "
						+ DriverManager.getDriver().getTitle());
				return;
			}
		}
		System.err.println("⚠️ [switchToNewWindow] No new window found.");
		throw new NoSuchWindowException("No new window found.");
	}

	public static void closeCurrentWindow() {
		System.out.println("❌ [closeCurrentWindow] Closing the current window with title: "
				+ DriverManager.getDriver().getTitle());
		DriverManager.getDriver().close();
	}

	public static void closeAllWindowsExceptCurrent() {
		String currentWindowHandle = DriverManager.getDriver().getWindowHandle();
		Set<String> windowHandles = DriverManager.getDriver().getWindowHandles();
		for (String handle : windowHandles) {
			if (!handle.equals(currentWindowHandle)) {
				DriverManager.getDriver().switchTo().window(handle);
				DriverManager.getDriver().close();
				System.out.println("❌ [closeAllWindowsExceptCurrent] Closed window with title: "
						+ DriverManager.getDriver().getTitle());
			}
		}
		DriverManager.getDriver().switchTo().window(currentWindowHandle);
		System.out.println("❌️ [closeAllWindowsExceptCurrent] Switched back to current window with title: "
				+ DriverManager.getDriver().getTitle());
	}

	// --- JavaScript Execution ---

	public static Object executeJavaScript(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [executeJavaScript] JavaScript code cannot be null or empty.");
			throw new IllegalArgumentException("JavaScript code cannot be null or empty.");
		}
		String script = inputParams[0].trim();
		JavascriptExecutor jsExecutor = (JavascriptExecutor) DriverManager.getDriver();
		Object result = jsExecutor.executeScript(script, getOptionalWebElement(inputParams, 1));
		System.out.println(
				"🚀 [executeJavaScript] Executed script: " + script + (result != null ? ", Result: " + result : ""));
		return result;
	}

	private static WebElement getOptionalWebElement(String[] inputParams, int index) {
		if (inputParams.length > index && inputParams[index] != null && !inputParams[index].trim().isEmpty()) {
			return searchElement(inputParams[index].trim());
		}
		return null;
	}

	// --- Scrolling ---

	public static void scrollToElement(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [scrollToElement] Locator key cannot be null or empty.");
			throw new IllegalArgumentException("Locator key cannot be null or empty.");
		}
		WebElement element = searchElement(inputParams[0].trim());
		JavascriptExecutor jsExecutor = (JavascriptExecutor) DriverManager.getDriver();
		jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
		System.out.println("📜 [scrollToElement] Scrolled to element with locator: " + inputParams[0].trim());
	}

	public static void scrollToTop() {
		JavascriptExecutor jsExecutor = (JavascriptExecutor) DriverManager.getDriver();
		jsExecutor.executeScript("window.scrollTo(0, 0);");
		System.out.println("📜 [scrollToTop] Scrolled to the top of the page.");
	}

	public static void scrollToBottom() {
		JavascriptExecutor jsExecutor = (JavascriptExecutor) DriverManager.getDriver();
		jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
		System.out.println("📜 [scrollToBottom] Scrolled to the bottom of the page.");
	}

	// --- Waiting ---

	public static void sleep(String... inputParams) throws InterruptedException {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [sleep] Wait time in seconds cannot be null or empty.");
			throw new IllegalArgumentException("Wait time in seconds cannot be null or empty.");
		}
		String waitTimeStr = inputParams[0].trim();
		int waitTime = Integer.parseInt(waitTimeStr);
		int waitTimeInMilliseconds = waitTime * 1000;
		System.out.println("⏳ [sleep] Waiting for " + waitTime + " seconds (" + waitTimeInMilliseconds + " ms)");
		Thread.sleep(waitTimeInMilliseconds);
	}

	public static WebElement waitForElementVisible(String... inputParams) {
		return waitForElement(ExpectedConditions.visibilityOfElementLocated(getByLocator(inputParams)));
	}

	public static WebElement waitForElementClickable(String... inputParams) {
		return waitForElement(ExpectedConditions.elementToBeClickable(getByLocator(inputParams)));
	}

	public static WebElement waitForElementPresent(String... inputParams) {
		return waitForElement(ExpectedConditions.presenceOfElementLocated(getByLocator(inputParams)));
	}

	private static WebElement waitForElement(ExpectedCondition<WebElement> condition) {
		WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
		return wait.until(condition);
	}

	public static WebElement fluentWaitForElementVisible(String... inputParams) {
		return fluentWaitForElement(ExpectedConditions.visibilityOfElementLocated(getByLocator(inputParams)));
	}

	public static WebElement fluentWaitForElementClickable(String... inputParams) {
		return fluentWaitForElement(ExpectedConditions.elementToBeClickable(getByLocator(inputParams)));
	}

	public static WebElement fluentWaitForElementPresent(String... inputParams) {
		return fluentWaitForElement(ExpectedConditions.presenceOfElementLocated(getByLocator(inputParams)));
	}

	private static WebElement fluentWaitForElement(ExpectedCondition<WebElement> condition) {
		FluentWait<WebDriver> wait = new FluentWait<>(DriverManager.getDriver())
				.withTimeout(Duration.ofSeconds(DEFAULT_TIMEOUT)).pollingEvery(Duration.ofMillis(500))
				.ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class);

		return wait.until(condition);
	}

	// --- Validation ---

	public static void validateSuccessMessage(String... inputParams) {
		if (inputParams == null || inputParams.length < 2 || inputParams[0] == null || inputParams[0].trim().isEmpty()
				|| inputParams[1] == null || inputParams[1].trim().isEmpty()) {
			System.err.println("⚠️ [validateSuccessMessage] Expected message and locator key cannot be null or empty.");
			throw new IllegalArgumentException("Expected message and locator key cannot be null or empty.");
		}
		String expectedMessage = inputParams[0].trim();
		String locatorKey = inputParams[1].trim();
		WebElement element = searchElement(locatorKey);
		String actualMessage = element.getText();
		System.out.println("✅ [validateSuccessMessage] Validating if element with locator '" + locatorKey
				+ "' contains text: '" + expectedMessage + "'. Actual: '" + actualMessage + "'.");

		if (!actualMessage.contains(expectedMessage)) {
			throw new AssertionError("❌ Validation Failed: Expected '" + expectedMessage + "' but got '" + actualMessage
					+ "' for element with locator '" + locatorKey + "'.");
		}
		System.out.println("✅ Validation Passed: Message is displayed.");
	}

	public static void validateElementText(String... inputParams) {
		if (inputParams == null || inputParams.length < 2 || inputParams[0] == null || inputParams[0].trim().isEmpty()
				|| inputParams[1] == null || inputParams[1].trim().isEmpty()) {
			System.err.println("⚠️ [validateElementText] Expected text and locator key cannot be null or empty.");
			throw new IllegalArgumentException("Expected text and locator key cannot be null or empty.");
		}
		String locatorKey = inputParams[0].trim();
		String expectedText = inputParams[1].trim();
		WebElement element = searchElement(locatorKey);
		String actualText = element.getText();
		System.out.println("✅ [validateElementText] Validating if element with locator '" + locatorKey
				+ "' has exact text: '" + expectedText + "'. Actual: '" + actualText + "'.");

		if (!actualText.equals(expectedText)) {
			throw new AssertionError("❌ Validation Failed: Expected text '" + expectedText + "' but got '" + actualText
					+ "' for element with locator '" + locatorKey + "'.");
		}
		System.out.println("✅ Validation Passed: Element text is as expected.");
	}

	// --- Browser Management ---

	public static void closeBrowser(String... inputParams) {
		System.out.println("🛑 [closeBrowser] Closing the browser.");
		DriverManager.quitDriver();
	}

	// --- Internal Helper Functions ---

	private static WebElement searchElement(String locatorKey) {
		Locator locator = LocatorReader.getLocator(locatorKey);
		if (locator == null) {
			System.err.println("⚠️ [searchElement] Locator key '" + locatorKey + "' not found in locator file.");
			throw new NoSuchElementException("Locator key '" + locatorKey + "' not found in locator file.");
		}
		System.out.println(
				"🔎 [searchElement] Resolved Locator: " + locator.getValue() + ", Strategy: " + locator.getStrategy());
		return DriverManager.getDriver().findElement(getBy(locator.getStrategy(), locator.getValue()));
	}

	private static By getBy(String strategy, String locatorValue) {
		switch (strategy.toLowerCase()) {
		case "id":
			return By.id(locatorValue);
		case "name":
			return By.name(locatorValue);
		case "css":
		case "cssselector":
			return By.cssSelector(locatorValue);
		case "class":
		case "classname":
			return By.className(locatorValue);
		case "tag":
		case "tagname":
			return By.tagName(locatorValue);
		case "linktext":
			return By.linkText(locatorValue);
		case "partiallinktext":
			return By.partialLinkText(locatorValue);
		case "xpath":
		default:
			return By.xpath(locatorValue);
		}
	}

	private static By getByLocator(String... inputParams) {
		if (inputParams == null || inputParams.length == 0 || inputParams[0] == null
				|| inputParams[0].trim().isEmpty()) {
			System.err.println("⚠️ [getByLocator] Locator key cannot be null or empty.");
			throw new IllegalArgumentException("Locator key cannot be null or empty.");
		}
		String locatorKey = inputParams[0].trim();
		Locator locator = LocatorReader.getLocator(locatorKey);
		if (locator == null) {
			System.err.println("⚠️ [getByLocator] Locator key '" + locatorKey + "' not found in locator file.");
			throw new NoSuchElementException("Locator key '" + locatorKey + "' not found in locator file.");
		}
		return getBy(locator.getStrategy(), locator.getValue());
	}
}