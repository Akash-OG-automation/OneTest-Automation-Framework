package com.onetest.runner;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.onetest.utils.ConfigurationManager;
import com.onetest.utils.ExcelReader;
import com.onetest.utils.ScreenshotUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

public class EngineManager {

	// Update FUNCTION_PACKAGE to include both packages, separated by a delimiter (e.g., semicolon)
	private static final String FUNCTION_PACKAGE = "com.onetest.keywords;com.onetest.appfunctions";
	private static boolean STOP_ON_FAILURE;
	private static boolean PARALLEL_EXECUTION;
	private static final Logger logger = Logger.getLogger(EngineManager.class);

	// ExtentReport instance
	private static ExtentReports extent;
	private static String reportPath;

	// ThreadLocal for ExtentTest
	private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

	public static void main(String[] args) {
		try {
			logger.info("🚀 Running Engine Manager...");

			// Load configuration
			ConfigurationManager.loadConfigurations("config/Config.txt");
			STOP_ON_FAILURE = Boolean.parseBoolean(ConfigurationManager.getValue("STOP_ON_FAILURE"));
			PARALLEL_EXECUTION = Boolean.parseBoolean(ConfigurationManager.getValue("PARALLEL_EXECUTION_ENABLED"));
			int parallelThreads = Integer.parseInt(ConfigurationManager.getValue("PARALLEL_THREADS"));
			String reportDir = ConfigurationManager.getValue("REPORT_PATH");

			// Generate unique report name
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String timestamp = sdf.format(new Date());
			reportPath = Paths.get(reportDir, "TestReport_" + timestamp + ".html").toString();

			// Initialize ExtentReports
			ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
			extent = new ExtentReports();
			extent.attachReporter(spark);
			spark.config().setTheme(Theme.STANDARD);
			spark.config().setDocumentTitle("OneTest Automation Report");
			spark.config().setReportName(
					(PARALLEL_EXECUTION ? "Parallel" : "Sequential") + " Execution Summary - " + timestamp);

			// Inject JavaScript for image expansion (Delegated listener on document)
			spark.config().setJs("$(document).ready(function() {\n"
					+ "    $(document).on('click', '.expandable-image', function() {\n" // Delegating to document
					+ "        console.log('Expandable image clicked (delegated on document)!');"
					+ "        var fullImageSrc = $(this).data('fullimage');\n"
					+ "        var overlay = $('<div id=\"image-overlay\" style=\"position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.8); display: flex; justify-content: center; align-items: center; z-index: 1000; cursor: pointer;\" onclick=\"$(\\'#image-overlay\\').remove();\">' +\n"
					+ "                                  '<img src=\"' + fullImageSrc + '\" style=\"max-width: 90%; max-height: 90%;\">' +\n"
					+ "                                '</div>');\n" + "        $('body').append(overlay);\n"
					+ "    });\n" + "});");

			extent.setSystemInfo("Framework", "OneTest");
			extent.setSystemInfo("Environment", ConfigurationManager.getValue("ENVIRONMENT"));
			extent.setSystemInfo("Parallel Execution", String.valueOf(PARALLEL_EXECUTION));
			if (PARALLEL_EXECUTION) {
				extent.setSystemInfo("Parallel Threads", String.valueOf(parallelThreads));
			}

			// Read test data
			List<Map<String, String>> testSuites = ExcelReader.readTestSuiteSheet();
			List<Map<String, String>> testCases = ExcelReader.readTestCasesSheet();
			List<Map<String, String>> testSteps = ExcelReader.readTestStepsSheet();
			List<Map<String, String>> bpcs = ExcelReader.readBPCSheet();

			List<String> testSuitesToExecute = testSuites.stream()
					.filter(suite -> "Y".equalsIgnoreCase(suite.get("Flag"))).map(suite -> suite.get("TestSuiteName"))
					.collect(Collectors.toList());

			if (testSuitesToExecute.isEmpty()) {
				logger.warn("⚠️ No test suites marked for execution.");
				return;
			}

			logger.info("\n🧪 Running TestSuite(s): " + testSuitesToExecute);

			List<Map<String, String>> testCasesToExecute = testCases.stream().filter(
							tc -> "Y".equalsIgnoreCase(tc.get("Flag")) && testSuitesToExecute.contains(tc.get("TestSuiteName")))
					.collect(Collectors.toList());

			if (testCasesToExecute.isEmpty()) {
				logger.warn("⚠️ No test cases marked for execution.");
				return;
			}

			if (PARALLEL_EXECUTION) {
				ExecutorService executorService = Executors.newFixedThreadPool(parallelThreads);
				List<TestRunner> testRunners = new ArrayList<>();

				for (Map<String, String> testCase : testCasesToExecute) {
					testRunners.add(new TestRunner(testCase, testSteps, bpcs, extent, true)); // Pass true for parallel
					// execution
				}

				try {
					executorService.invokeAll(testRunners); // Submit all tasks and wait for completion
				} catch (InterruptedException e) {
					logger.error("Execution interrupted.", e);
					Thread.currentThread().interrupt();
					// Restore interrupted status
					Thread.currentThread().interrupt();
				} finally {
					executorService.shutdown();
				}
				logExecutionSummary(testRunners);
			} else {
				List<TestRunner> testRunners = new ArrayList<>();
				for (Map<String, String> testCase : testCasesToExecute) {
					TestRunner runner = new TestRunner(testCase, testSteps, bpcs, extent, false); // Pass false for
					// sequential
					runner.call(); // Execute sequentially
					testRunners.add(runner);
				}
				logExecutionSummary(testRunners);
			}

			// Flush Extent Report
			extent.flush();

		} catch (Exception e) {
			logger.fatal("❌ Fatal error in EngineManager:", e);
		}
	}

	private static class TestRunner implements java.util.concurrent.Callable<Void> {
		private final Map<String, String> testCaseData;
		private final List<Map<String, String>> testStepsData;
		private final List<Map<String, String>> bpcData;
		private final ExtentReports extent;
		private final boolean isParallel;

		public TestRunner(Map<String, String> testCaseData, List<Map<String, String>> testStepsData,
						  List<Map<String, String>> bpcData, ExtentReports extent, boolean isParallel) {
			this.testCaseData = testCaseData;
			this.testStepsData = testStepsData;
			this.bpcData = bpcData;
			this.extent = extent;
			this.isParallel = isParallel;
		}

		@Override
		public Void call() throws Exception {
			String testCaseName = testCaseData.get("TestCaseName");
			String description = testCaseData.get("Description");
			String threadInfo = isParallel ? " (Thread: " + Thread.currentThread().getName() + ")" : "";
			logger.info("\n🔷 Starting Test Case" + threadInfo + ": " + testCaseName + " - " + description);
			ExtentTest test = extent.createTest(testCaseName, description); // Set description during test creation
			extentTestThreadLocal.set(test); // Set the ExtentTest instance for the current thread

			List<Map<String, String>> stepsForTestCase = testStepsData.stream()
					.filter(step -> testCaseName.equals(step.get("TestCaseName"))).collect(Collectors.toList());
			logger.info("🔢 Total Steps for " + testCaseName + ": " + stepsForTestCase.size());

			boolean allStepsPassed = true;
			long startTime = System.currentTimeMillis();

			for (Map<String, String> step : stepsForTestCase) {
				if (!"Y".equalsIgnoreCase(step.get("Flag")))
					continue;

				String methodName = step.get("MethodName");
				String stepDescription = step.get("StepDescription");
				String screenshotNeeded = step.get("ScreenshotNeeded");

				try {
					if (methodName.startsWith("bpc")) {
						String bpcName = methodName;
						List<Map<String, String>> bpcSteps = bpcData.stream().filter(b -> b.get("BPC").equals(bpcName))
								.collect(Collectors.toList());
						for (Map<String, String> bpcStep : bpcSteps) {
							processStep(bpcStep, testCaseData, true); // isBPC = true
						}
					} else {
						processStep(step, testCaseData, false); // isBPC = false
					}
				} catch (Exception e) {
					allStepsPassed = false;
					logger.error("❌ Step Failed" + threadInfo + ": " + methodName + " - " + e.getMessage(), e);
					embedScreenshot(stepDescription, screenshotNeeded, e.getMessage(), Status.FAIL);

					if (STOP_ON_FAILURE) {
						logger.info("🛑 Stopping execution for current test case" + threadInfo + " due to failure.");
						try {
							invokeMethod("closeBrowser", new ArrayList<>());
						} catch (Exception closeEx) {
							logger.warn("⚠️ Failed to close browser" + threadInfo + ": " + closeEx.getMessage());
							getCurrentTest().log(Status.WARNING, "Failed to close browser: " + closeEx.getMessage());
						}
						break;
					}
				}
			}

			long endTime = System.currentTimeMillis();
			String result = allStepsPassed ? "✅ PASSED" : "❌ FAILED";
			long executionTime = endTime - startTime;

			if (allStepsPassed) {
				getCurrentTest().pass("Test Case PASSED in " + executionTime + " ms");
			} else {
				getCurrentTest().fail("Test Case FAILED in " + executionTime + " ms");
			}

			// Store the result for summary
			testCaseData.put("executionResult", result);
			testCaseData.put("executionTime", String.valueOf(executionTime));

			return null;
		}

		private ExtentTest getCurrentTest() {
			return extentTestThreadLocal.get();
		}

		private void processStep(Map<String, String> stepData, Map<String, String> testCaseData, boolean isBPC)
				throws Exception {
			String stepDescription = stepData.get("StepDescription");
			String methodName = stepData.get("MethodName");
			String screenshotNeeded = stepData.get("ScreenshotNeeded");
			String stepNumberKey = isBPC ? "BPCStepNumber" : "StepNumber";
			String threadInfo = isParallel ? " (Thread: " + Thread.currentThread().getName() + ")" : "";
			String logPrefix = isParallel ? "(Thread: " + Thread.currentThread().getName() + ") " : "";

			logger.info(logPrefix + "➡️ " + (isBPC ? "BPC Step" : "Step") + ": " + stepData.get(stepNumberKey) + " - "
					+ stepDescription);
			logger.info(logPrefix + "🔧 Executing Method: " + methodName);

			List<String> resolvedParams = resolveStepParameters(stepData, testCaseData, isBPC);
			invokeMethod(methodName, resolvedParams);

			logger.info(logPrefix + "✅ " + (isBPC ? "BPC Step" : "Step") + " Passed");
			embedScreenshot(stepDescription, screenshotNeeded, null, Status.PASS);
		}

		private void embedScreenshot(String description, String screenshotFlag, String errorMessage, Status status) {
			String threadInfo = isParallel ? " (Thread: " + Thread.currentThread().getName() + ")" : "";
			String logPrefix = isParallel ? "(Thread: " + Thread.currentThread().getName() + ") " : "";
			if ("Y".equalsIgnoreCase(screenshotFlag)) {
				String screenshotPath = ScreenshotUtil.captureScreenshot(description);
				try {
					byte[] base64ImageBytes = Files.readAllBytes(Paths.get(screenshotPath));
					String base64Image = java.util.Base64.getEncoder().encodeToString(base64ImageBytes);
					String htmlContent = "<div style='margin-top: 5px; border: 1px solid #ccc; padding: 5px; width: 300px;'>"
							+ "<img src='data:image/png;base64," + base64Image
							+ "' height='150' class='expandable-image' data-fullimage='data:image/png;base64,"
							+ base64Image + "' style='cursor: pointer; width: 100%; height: auto;'/>" + "</div>";
					if (status == Status.PASS) {
						getCurrentTest().pass(description + "<br>" + htmlContent);
					} else {
						getCurrentTest().fail(description + " | Error: " + errorMessage + "<br>" + htmlContent);
					}
				} catch (IOException e) {
					logger.error(logPrefix + "❌ Error readingscreenshot file: " + screenshotPath, e);
					getCurrentTest().log(status, description + (errorMessage != null ? " | Error: " + errorMessage : "")
							+ " (Failed to embed screenshot)");
				}
			} else {
				getCurrentTest().log(status, description + (errorMessage != null ? " | Error: " + errorMessage : ""));
			}
		}

		private void invokeMethod(String methodName, List<String> parameters) throws Exception {
			String threadInfo = isParallel ? " (Thread: " + Thread.currentThread().getName() + ")" : "";
			String logPrefix = isParallel ? "(Thread: " + Thread.currentThread().getName() + ") " : "";
			String[] packageNames = FUNCTION_PACKAGE.split(";");
			for (String packageName : packageNames) {
				String fullClassName = packageName + "." + getClassNameFromMethod(methodName);
				try {
					Class<?> clazz = Class.forName(fullClassName);
					Method method = clazz.getMethod(methodName, String[].class);
					logger.info(logPrefix + "🧠 Invoking: " + methodName + " from " + fullClassName + " with parameters: " + parameters);
					method.invoke(null, (Object) parameters.toArray(new String[0]));
					return; // Method found and invoked, so exit the loop
				} catch (ClassNotFoundException e) {
					// Class not found in this package, try the next one
				} catch (NoSuchMethodException e) {
					// Method not found in this class, try the next package
				} catch (Exception e) {
					throw new Exception(logPrefix + "❌ Error invoking method: " + methodName + " in " + fullClassName, e);
				}
			}
			throw new NoSuchMethodException(logPrefix + "❌ Method not found: " + methodName + " in any of the specified packages: " + FUNCTION_PACKAGE);
		}

		private String getClassNameFromMethod(String methodName) {
			if (methodName.matches("^[a-z]+[A-Z].*")) {
				// Assuming camelCase, the class name might be the first part capitalized
				if (methodName.startsWith("set") || methodName.startsWith("get") || methodName.startsWith("is") || methodName.startsWith("click") || methodName.startsWith("select") || methodName.startsWith("hover") || methodName.startsWith("doubleClick") || methodName.startsWith("rightClick") || methodName.startsWith("navigate") || methodName.startsWith("refresh") || methodName.startsWith("accept") || methodName.startsWith("dismiss") || methodName.startsWith("switchTo")) {
					return "CommonFunctions";
				} else {
					return "AppFunctions"; // Default to AppFunctions if the pattern doesn't match common prefixes
				}
			} else if (methodName.matches("^[A-Z].*")) {
				// Assuming PascalCase, likely an AppFunction
				return "AppFunctions";
			} else {
				// Fallback if the naming convention is unclear
				// You might need a more sophisticated way to map method names to classes
				return "CommonFunctions"; // Default fallback
			}
		}


		private List<String> resolveStepParameters(Map<String, String> step, Map<String, String> testCase,
												   boolean isBPC) {
			List<String> params = new ArrayList<>();
			String prefix = step.keySet().stream().anyMatch(k -> k.startsWith("BPCIP_")) ? "BPCIP_" : "TSIP_";
			String logPrefix = isParallel ? "(Thread: " + Thread.currentThread().getName() + ") " : "";

			for (int i = 1; i <= 20; i++) {
				String key = prefix + i;
				if (!step.containsKey(key))
					continue;

				String rawValue = step.get(key);
				if (rawValue == null || rawValue.trim().isEmpty())
					continue;

				String actualValue;
				if (rawValue.startsWith("::")) {
					String tcKey = rawValue.substring(2);
					actualValue = testCase.getOrDefault(tcKey, "").trim();
				} else {
					actualValue = rawValue.trim();
				}

				if (actualValue.startsWith("onetest.")) {
					String configValue = ConfigurationManager.getValue(actualValue);
					if (configValue != null && !configValue.isEmpty()) {
						actualValue = configValue;
					} else {
						logger.warn(logPrefix + "⚠️ Config key not found: " + actualValue);
					}
				}

				if (!actualValue.isEmpty()) {
					params.add(actualValue);
				}
			}
			return params;
		}
	}

	private static void logExecutionSummary(List<TestRunner> runners) {
		int passedCount = 0;
		int failedCount = 0;
		List<String> summary = new ArrayList<>();

		for (TestRunner runner : runners) {
			Map<String, String> testCaseData = runner.testCaseData;
			String testCaseName = testCaseData.get("TestCaseName");
			String result = testCaseData.get("executionResult");
			String executionTime = testCaseData.get("executionTime");

			summary.add(testCaseName + " - " + result + " (" + executionTime + " ms)");

			if ("✅ PASSED".equals(result)) {
				passedCount++;
			} else if ("❌ FAILED".equals(result)) {
				failedCount++;
			}
		}

		int totalExecuted = passedCount + failedCount;
		double passPercentage = totalExecuted > 0 ? ((double) passedCount / totalExecuted) * 100 : 0.0;

		logger.info("============================ 📋 Test Execution Summary ===========================");
		for (String line : summary) {
			logger.info(line);
		}
		logger.info("==================================================================================");
		logger.info("📊 Total Test Cases Executed: " + totalExecuted);
		logger.info("✅ Passed: " + passedCount);
		logger.info("❌ Failed: " + failedCount);
		logger.info(String.format("📈 Pass Percentage: %.2f%%", passPercentage));
		logger.info("==================================================================================");
	}
}