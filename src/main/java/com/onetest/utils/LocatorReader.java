package com.onetest.utils;

import java.io.*;
import java.util.*;

public class LocatorReader {

    private static final String LOCATOR_FILE;
    private static final String DEFAULT_STRATEGY = "xpath";
    
    static {
    	LOCATOR_FILE = ConfigurationManager.getValue("PROPERTY_FILE");

        if (LOCATOR_FILE == null || LOCATOR_FILE.isEmpty()) {
            throw new RuntimeException("❌ 'PROPERTY_FILE' not defined in Configuration.txt");
        }
    }

    private static final Map<String, Locator> locatorMap = new HashMap<>();

    static {
        loadLocators();
    }

    private static void loadLocators() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOCATOR_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("=", 2);
                if (parts.length < 2) {
                    System.err.println("❗ Invalid line format: " + line);  // More specific error log
                    continue;
                }

                String key = parts[0].trim();
                String[] valueParts = parts[1].trim().split(":", 2);

                if (valueParts.length < 2) {
                    System.err.println("❗ Invalid locator format in Properties.txt for key: " + key);
                    continue;
                }

                String locatorValue = valueParts[0].trim();
                String strategy = valueParts[1].trim().toLowerCase();

                locatorMap.put(key, new Locator(locatorValue, strategy));
                System.out.println("✅ Locator loaded: " + key + " → " + locatorValue + " (" + strategy + ")");
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to load locators: " + e.getMessage());
        }
    }


    public static Locator getLocator(String keyOrRaw) {
        if (keyOrRaw.startsWith("//") || keyOrRaw.startsWith("(")) {
            // If direct XPath is passed instead of key
            return new Locator(keyOrRaw, DEFAULT_STRATEGY);
        }

        Locator locator = locatorMap.get(keyOrRaw);
        if (locator == null) {
            throw new RuntimeException("❌ Locator key not found in Properties.txt: " + keyOrRaw);
        }
        return locator;
    }

    public static class Locator {
        private final String value;
        private final String strategy;

        public Locator(String value, String strategy) {
            this.value = value;
            this.strategy = strategy;
        }

        public String getValue() {
            return value;
        }

        public String getStrategy() {
            return strategy;
        }

        @Override
        public String toString() {
            return value + ":" + strategy;
        }
    }
}
