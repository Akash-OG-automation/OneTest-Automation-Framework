# 🚀 OneTest Automation Framework 🚀

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![LinkedIn](https://img.shields.io/badge/-Akash%20Oganiya-blue?style=flat-square&logo=linkedin)](https://www.linkedin.com/in/akash-oganiya-b336b934b/)

## ✨ Overview ✨

The OneTest Automation Framework is a robust and versatile **keyword-driven** and **data-driven** test automation solution 🛠️ engineered with Java and a suite of industry-standard open-source technologies. It's designed to simplify the automation of various applications by abstracting interactions and verifications into reusable keywords defined and orchestrated through Excel sheets 📊. This architecture promotes ease of use, maintainability, and scalability for automation projects of any size.

Key features include:

* **🔑 Keyword-Driven Testing:** Test execution is driven by keywords defined in Excel, with their implementations residing in the `com.onetest.keywords.CommonFunctions` class. This allows non-technical users to contribute to test automation.
* **💾 Data-Driven Testing:** All test-related data, including test suites, test cases, and test steps, is managed externally in Excel files 📝, enabling efficient execution of tests with diverse datasets without modifying the core automation scripts.
* **🌐 Selenium Integration:** Employs Selenium WebDriver to facilitate interactions with web browsers (Chrome ⚙️, Firefox 🦊, Edge 🌐), managed seamlessly through WebDriverManager.
* **🧪 TestNG Integration:** While the primary execution flow is managed by `EngineManager.java`, the framework incorporates TestNG (`org.testng`), which could be leveraged for more intricate test management and execution scenarios in future enhancements.
* **📊 Extent Reports:** Generates detailed and visually appealing HTML reports 📈, providing insights into test execution status, step-by-step results, and embedded screenshots 📸 of failures, directly integrated within the `EngineManager.java`.
* **📈 Excel Integration:** Utilizes the `com.onetest.utils.ExcelReader` utility to parse test configurations and steps from Excel files, making test definition straightforward.
* **⚙️ Centralized Configuration:** The `com.onetest.utils.ConfigurationManager` handles the loading and retrieval of configuration parameters from the `Config.txt` file, allowing for easy management of environment-specific settings.
* **📍 Element Locators Management:** The `com.onetest.utils.LocatorReader` reads and manages element locators from the `Properties.txt` file, supporting various locator strategies (ID, name, CSS, XPath, etc.), enhancing test stability.
* **📦 Automatic WebDriver Management:** Leverages WebDriverManager (`io.github.bonigarcia.wdm`) to automatically handle browser driver setup, eliminating manual driver management.
* **🖼️ Screenshot on Failure:** The `com.onetest.utils.ScreenshotUtil` automatically captures screenshots when a test step fails, saving them in a configurable directory and embedding them in the Extent Reports.
* **⚡ Parallel Execution:** Supports concurrent execution of test cases using a configurable number of threads, significantly reducing the overall test execution time.
* **🧩 Business Process Components (BPCs):** Enables the creation of reusable sequences of keywords (defined in the "BPC" Excel sheet), which can be invoked from test steps, promoting modularity and reducing redundancy in test scripts.

## 🚀 Getting Started 🚀

These instructions will guide you on how to set up and run the OneTest Automation Framework on your local machine.

### 🛠️ Prerequisites 🛠️

* **☕ Java Development Kit (JDK):** Ensure you have a compatible JDK installed (recommended version >= 1.8).
* **🛠️ Maven:** This project is built using Maven for dependency management and build automation. Ensure Maven is installed and configured in your environment.
* **💻 Web Browser:** Make sure you have the necessary web browsers (Chrome, Firefox, Edge) installed. The framework will automatically handle the required browser drivers.

### ⚙️ Installation ⚙️

1.  **Clone the repository:**
    ```bash
    git clone [YOUR_REPOSITORY_URL]
    ```
    *(Replace `[YOUR_REPOSITORY_URL]` with the actual URL of your GitHub repository once you've created it.)*

2.  **Navigate to the project directory:**
    ```bash
    cd [YOUR_PROJECT_DIRECTORY]
    ```
    *(Replace `[YOUR_PROJECT_DIRECTORY]` with the name of your project directory.)*

3.  **Build the project using Maven:**
    ```bash
    mvn clean install
    ```
    This command will download all dependencies and compile the project.

### ⚙️ Configuration ⚙️

The framework's behavior is controlled through two primary configuration files that should be located within your project directory after cloning:

* **`Config.txt`:** This file contains settings such as execution control (`STOP_ON_FAILURE`, `PARALLEL_EXECUTION_ENABLED`, `PARALLEL_THREADS`), reporting paths (`REPORT_PATH`, `SCREENSHOT_DIR`), environment details (`ENVIRONMENT`), default timeouts (`DEFAULT_TIMEOUT`), and custom configuration keys (e.g., browser preferences, application URLs, credentials). **After cloning the repository, you should place your `Config.txt` file at the root level of your project or in a dedicated `config` folder. If you choose a different location, you will need to update the file path in the `com.onetest.runner.EngineManager.java` file accordingly.**
* **`Properties.txt`:** This file stores element locators using a `locatorKey = locatorValue:strategy` format (e.g., `usernameField = username:name`). **After cloning, place your `Properties.txt` file at the root level of your project or in a dedicated `config` folder. If you choose a different location, you will need to update the file path in the `com.onetest.utils.LocatorReader.java` file accordingly.**

**Note:** The `EXCEL_PATH` property within the `Config.txt` file specifies the location of your Excel data file(s). It's recommended to keep your Excel file(s) within your project structure (e.g., in a `testdata` folder) and use a relative path in `Config.txt` (e.g., `testdata/YourTestData.xlsx`).

### ▶️ Running the Tests ▶️

Test execution is initiated by running the `EngineManager.java` class, which orchestrates the test flow based on the Excel files.

You can run the tests from your IDE (IntelliJ) or using Maven:

* **From IntelliJ:**
    1.  Open the project in IntelliJ.
    2.  Navigate to the `com.onetest.runner` package and locate the `EngineManager.java` file.
    3.  Right-click on the `EngineManager.java` file.
    4.  Select "Run 'EngineManager.main()'".

* **Using Maven:**
    1.  Open a terminal or command prompt.
    2.  Navigate to the root directory of your project.
    3.  Execute the following command:
        ```bash
        mvn exec:java -Dexec.mainClass="com.onetest.runner.EngineManager"
        ```

### 📄 Test Data Files 📄

The framework relies on one or more Excel files to manage test definitions and data. The path to the primary Excel file is configured in the `Config.txt` file using the `EXCEL_PATH` property. This Excel file (or potentially multiple files, if your framework is extended to support that) should contain the following sheets:

* **TestSuite (Sheet Name: `TestSuite`):** This sheet lists the test suites to be executed and a flag to indicate whether a suite should be included in the current run.
    * **Key Columns:** `TestSuiteName` (unique name of the test suite), `Flag` (`Y` to execute, `N` to skip).
* **TestCases (Sheet Name: `TestCases`):** This sheet contains the definitions of individual test cases, including their association with a test suite, a descriptive name, and execution control.
    * **Key Columns:** `TestSuiteName` (name of the test suite this test case belongs to), `TestCaseName` (unique name of the test case), `Description` (brief description of the test case), `Flag` (`Y` to execute, `N` to skip). Optionally, it may contain columns for test case-specific input parameters (e.g., `TCIP_1`, `TCIP_2`, etc.).
* **TestSteps (Sheet Name: `TestSteps`):** This sheet defines the sequence of automation steps for each test case. It specifies the keyword to be executed, a description of the step, and any necessary input parameters.
    * **Key Columns:** `TestCaseName` (name of the test case this step belongs to), `StepNumber` (sequential number of the step within the test case), `StepDescription` (description of the automation step), `MethodName` (the keyword to be executed, corresponding to a method in `com.onetest.keywords.CommonFunctions`), `Flag` (`Y` to execute the step, `N` to skip), `ScreenshotNeeded` (`Y` to capture a screenshot on failure, `N` otherwise), and input parameters (`TSIP_1` to `TSIP_n`).
* **BPC (Sheet Name: `BPC`):** This sheet contains definitions for reusable Business Process Components, which are sequences of keywords that can be called from test steps.
    * **Key Columns:** `BPC` (unique name of the Business Process Component), `BPCStepNumber` (sequential number of the step within the BPC), `StepDescription` (description of the BPC step), `MethodName` (the keyword to be executed within the BPC), `Flag` (`Y` to execute the BPC step, `N` to skip), `ScreenshotNeeded` (`Y` to capture a screenshot on failure, `N` otherwise), and input parameters (`BPCIP_1` to `BPCIP_n`).

**Important:** Ensure that the sheet names and column headers in your Excel file(s) match these conventions for the framework to read and execute your tests correctly. The path to your Excel file(s) should be accurately specified in the `EXCEL_PATH` property within the `Config.txt` file, ideally using a relative path within your project.

### 💡 Usage 💡

To create and execute automated tests:

1.  **⚙️ Configure `Config.txt`:** Set the `EXCEL_PATH` to the location of your Excel data file(s) (using a relative path is recommended) and `PROPERTY_FILE` to the location of your `Properties.txt` file (again, relative path is better). Adjust other configuration settings as needed.
2.  **📍 Define Locators:** Add or modify element locators in the `Properties.txt` file using the `locatorKey = locatorValue:strategy` format.
3.  **🧪 Manage Test Suites:** In the "TestSuite" sheet of your Excel file(s), enable test suites for execution by setting the `Flag` to `Y`.
4.  **📝 Create Test Cases:** In the "TestCases" sheet, define your test scenarios, linking them to a `TestSuiteName`, providing a `TestCaseName` and `Description`, and setting the `Flag` to `Y` to include them in the run.
5.  **🪜 Implement Test Steps:** In the "TestSteps" sheet, for each `TestCaseName`, define a sequence of steps. Use the `MethodName` column to specify the keyword (a method in `com.onetest.keywords.CommonFunctions`), provide a `StepDescription`, configure screenshot capture on failure (`ScreenshotNeeded`), and pass input data using the `TSIP_` columns. You can reference data from the "TestCases" sheet using the `::` prefix (e.g., `::username`). Configuration values from `Config.txt` can be accessed using the `onetest.` prefix (e.g., `onetest.browser.default`).
6.  **🔑 Utilize Keywords:** The `com.onetest.keywords.CommonFunctions` class provides a comprehensive set of keywords for interacting with web elements and performing various automation tasks. Extend this class to add custom keywords specific to your application.
7.  **🧩 Employ BPCs:** Define reusable step sequences in the "BPC" sheet. You can then invoke a BPC from a "TestSteps" row by setting the `MethodName` to the `BPC` name. Pass parameters to BPC steps using the `BPCIP_` columns in the "BPC" sheet.

### 🛠️ Built With 🛠️

* [Java](https://www.oracle.com/java/)
* [Selenium WebDriver](https://www.selenium.dev/)
* [TestNG](https://testng.org/)
* [ExtentReports](https://www.extentreports.com/)
* [Apache POI](https://poi.apache.org/) (for Excel handling)
* [Apache Log4j](https://logging.apache.org/log4j/2.x/)
* [WebDriverManager](https://bonigarcia.dev/webdrivermanager/)
* [Apache Commons IO](https://commons.apache.org/proper/commons-io/) (for file utilities, like screenshot handling)

### 🤝 Contributing 🤝

Contributions are welcome! If you have suggestions or find issues, please feel free to open an issue on GitHub. If you'd like to contribute code, please fork the repository and submit a pull request with your changes.

### 📜 License 📜

This project is licensed under the [Apache License 2.0](https://opensource.org/licenses/Apache-2.0).

### 🧑‍💻 Contact 🧑‍💻

**Akash Oganiya**
📧 Email: [akashoganiya67@gmail.com](mailto:akashoganiya67@gmail.com)
🔗 LinkedIn: [https://www.linkedin.com/in/akash-oganiya-b336b934b/](https://www.linkedin.com/in/akash-oganiya-b336b934b/)

---