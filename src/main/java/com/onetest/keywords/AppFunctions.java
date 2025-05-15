package com.onetest.keywords;

public class AppFunctions extends CommonFunctions {

    // Demo code - will check github actions on this
    public static void loginToApplication(String usernameKey, String passwordKey) {
        System.out.println("📱 [AppFunctions] Logging in to the application...");
        setValue(usernameKey, "your_username_value");
        setValue(passwordKey, "your_password_value");
        clickElement("login_button");
        System.out.println("✅ [AppFunctions] Login process initiated.");
    }

    /**
     * Function verify user is redirected on OrangeHRM Dashboard after successful login
     */
   public static void verifyUserIsOnDashBoard(String... inputParams) {
       String locatorKey = inputParams[0];
       String expectedText = inputParams[1];
       validateElementText(locatorKey, expectedText);
   }

}