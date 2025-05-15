package com.onetest.utils;

import java.io.*;
import java.util.*;

public class ConfigurationManager {

    private static final Map<String, String> configMap = new HashMap<>();

    public static void loadConfigurations(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    configMap.put(parts[0].trim(), parts[1].trim().replaceAll("^\"|\"$", "")); // Remove quotes
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read configuration file: " + filePath, e);
        }
    }

    public static String getValue(String key) {
        return configMap.getOrDefault(key, "");
    }
}
