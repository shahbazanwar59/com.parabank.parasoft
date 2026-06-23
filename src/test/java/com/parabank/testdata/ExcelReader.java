package com.parabank.testdata;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Robust XLSX reader:
 * - Tries multiple classpath locations (with/without leading slash, under /testdata/)
 * - Falls back to filesystem `src/test/resources/...`
 * - Returns list of maps header->cellValue
 */
public class ExcelReader {

    private final DataFormatter formatter = new DataFormatter();
    private final String fileName;

    public ExcelReader() {
        // default filename: try to match common names and typos
        this("RegistratationTestData.xlsx");
    }

    public ExcelReader(String fileName) {
        this.fileName = fileName;
    }

    public List<Map<String, String>> readSheet(String sheetName) {
        List<Map<String, String>> rows = new ArrayList<>();
        List<String> triedLocations = new ArrayList<>();

        // candidate classpath paths
        String[] candidates = new String[] {
            "/" + fileName,
            fileName,
            "/testdata/" + fileName,
            "testdata/" + fileName,
            "/registration-data.xlsx",
            "/testdata/registration-data.xlsx",
            "registration-data.xlsx",
            "testdata/registration-data.xlsx"
        };

        InputStream is = null;
        for (String p : candidates) {
            triedLocations.add("classpath:" + p);
            is = getResourceAsStreamSafe(p);
            if (is != null) break;
        }

        // fallback to filesystem path (IDE runs)
        if (is == null) {
            String fsPath = "src/test/resources/testdata/" + fileName;
            triedLocations.add("file:" + fsPath);
            File f = new File(fsPath);
            if (!f.exists()) {
                // also try src/test/resources root
                fsPath = "src/test/resources/" + fileName;
                triedLocations.add("file:" + fsPath);
                f = new File(fsPath);
            }
            try {
                if (f.exists()) {
                    is = new FileInputStream(f);
                }
            } catch (Exception e) {
                // will handle below
            }
        }

        if (is == null) {
            String msg = "Excel resource not found. Tried:\n" + String.join("\n", triedLocations)
                + "\nPlace the file under src/test/resources (e.g. src/test/resources/testdata/" + fileName + ")"
                + " or update the ExcelReader to point to the correct filename.";
            throw new RuntimeException(msg);
        }

        try (InputStream in = is; XSSFWorkbook wb = new XSSFWorkbook(in)) {
            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) return rows;
            Row headerRow = sheet.getRow(sheet.getFirstRowNum());
            if (headerRow == null) return rows;

            List<String> headers = new ArrayList<>();
            headerRow.forEach(cell -> headers.add(formatter.formatCellValue(cell).trim()));

            for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                Map<String, String> map = new LinkedHashMap<>();
                for (int c = 0; c < headers.size(); c++) {
                    String h = headers.get(c);
                    String val = formatter.formatCellValue(row.getCell(c)).trim();
                    map.put(h, val);
                }
                boolean allEmpty = map.values().stream().allMatch(String::isEmpty);
                if (!allEmpty) rows.add(map);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read Excel sheet '" + sheetName + "': " + e.getMessage(), e);
        }
        return rows;
    }

    private InputStream getResourceAsStreamSafe(String path) {
        try {
            // Class loader lookup (no leading slash) and Class lookup (leading slash)
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) return is;
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(path.startsWith("/") ? path.substring(1) : path);
        } catch (Exception e) {
            return null;
        }
    }
}
