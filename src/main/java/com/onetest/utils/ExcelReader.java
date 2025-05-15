package com.onetest.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {

	private static final String EXCEL_PATH;

    static {
        EXCEL_PATH = ConfigurationManager.getValue("EXCEL_PATH");

        if (EXCEL_PATH == null || EXCEL_PATH.isEmpty()) {
            throw new RuntimeException("❌ 'EXCEL_PATH' not defined in Configuration.txt");
        }
    }

    private static Workbook getWorkbook(FileInputStream fis, String filePath) throws IOException {
        if (filePath.toLowerCase().endsWith("xlsx")) {
            return new XSSFWorkbook(fis);
        } else if (filePath.toLowerCase().endsWith("xls")) {
            return new HSSFWorkbook(fis);
        } else {
            throw new IllegalArgumentException("Invalid Excel file type. Supported: .xls, .xlsx");
        }
    }

    private static List<Map<String, String>> readSheet(String sheetName, String[] columnKeys, String paramPrefix) throws IOException {
        System.out.println("📄 Reading Sheet: " + sheetName);

        FileInputStream fis = new FileInputStream(new File(EXCEL_PATH));
        Workbook workbook = getWorkbook(fis, EXCEL_PATH);
        Sheet sheet = workbook.getSheet(sheetName);

        List<Map<String, String>> dataList = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // skip header row

            Map<String, String> dataMap = new LinkedHashMap<>();

            for (int i = 0; i < columnKeys.length; i++) {
                dataMap.put(columnKeys[i], formatter.formatCellValue(row.getCell(i)));
            }

            // Handle dynamic parameterized columns
            for (int i = columnKeys.length; i < row.getPhysicalNumberOfCells(); i++) {
                dataMap.put(paramPrefix + (i - columnKeys.length + 1), formatter.formatCellValue(row.getCell(i)));
            }

            dataList.add(dataMap);
        }

        workbook.close();
        return dataList;
    }

    public static List<Map<String, String>> readTestSuiteSheet() throws IOException {
        String[] columns = {"TestSuiteName", "Flag"};
        return readSheet("TestSuite", columns, "");
    }

    public static List<Map<String, String>> readTestCasesSheet() throws IOException {
        String[] columns = {"TestSuiteName", "TestCaseName", "Description", "StartingStep", "NoOfSteps", "Flag"};
        return readSheet("TestCases", columns, "TCIP_");
    }

    public static List<Map<String, String>> readTestStepsSheet() throws IOException {
        String[] columns = {"TestCaseName", "StepNumber", "StepDescription", "MethodName", "Flag", "ScreenshotNeeded"};
        return readSheet("TestSteps", columns, "TSIP_");
    }

    // Read BPC sheet that contains grouped repetitive steps
    public static List<Map<String, String>> readBPCSheet() throws IOException {
        String[] columns = {"BPC", "StepNumber", "StepDescription", "MethodName", "Flag", "ScreenshotNeeded"};
        return readSheet("BPC", columns, "BPCIP_");
    }
}
