package com.parabank.testutilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Minimal Apache POI helper for pulling tabular test data out of an .xlsx file.
 * Requires the Apache POI dependency (see pom.xml note in the chat response).
 */
public class ExcelUtils {

	private ExcelUtils() {
	}

	/**
	 * Reads a sheet into a list of rows, each represented as a header-name -> cell-value map.
	 * The first row of the sheet must contain column headers. Blank rows are skipped.
	 *
	 * @param resourcePathOrFilePath first tried as a classpath resource (e.g. a file under
	 *                               src/test/resources), then as a plain file system path.
	 * @param sheetName              name of the sheet to read.
	 */
	public static List<Map<String, String>> readSheetAsMaps(String resourcePathOrFilePath, String sheetName) {
		List<Map<String, String>> rows = new ArrayList<>();

		try (InputStream is = resolveInputStream(resourcePathOrFilePath);
				Workbook workbook = WorkbookFactory.create(is)) {

			Sheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				throw new IllegalArgumentException(
						"Sheet '" + sheetName + "' not found in " + resourcePathOrFilePath);
			}

			Row headerRow = sheet.getRow(0);
			if (headerRow == null) {
				throw new IllegalStateException("Header row missing in sheet '" + sheetName + "'");
			}

			int columnCount = headerRow.getLastCellNum();
			List<String> headers = new ArrayList<>();
			for (int c = 0; c < columnCount; c++) {
				headers.add(getCellValueAsString(headerRow.getCell(c)));
			}

			for (int r = 1; r <= sheet.getLastRowNum(); r++) {
				Row row = sheet.getRow(r);
				if (row == null) {
					continue;
				}
				Map<String, String> rowMap = new LinkedHashMap<>();
				boolean blank = true;
				for (int c = 0; c < columnCount; c++) {
					String value = getCellValueAsString(row.getCell(c));
					if (!value.isEmpty()) {
						blank = false;
					}
					rowMap.put(headers.get(c), value);
				}
				if (!blank) {
					rows.add(rowMap);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read Excel file: " + resourcePathOrFilePath, e);
		}

		return rows;
	}

	private static InputStream resolveInputStream(String path) throws IOException {
		InputStream classpathStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		if (classpathStream != null) {
			return classpathStream;
		}
		return new FileInputStream(path);
	}

	private static String getCellValueAsString(Cell cell) {
		if (cell == null) {
			return "";
		}
		CellType type = cell.getCellType();
		switch (type) {
			case STRING:
				return cell.getStringCellValue().trim();
			case NUMERIC:
				double num = cell.getNumericCellValue();
				if (num == Math.floor(num)) {
					return String.valueOf((long) num);
				}
				return String.valueOf(num);
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case FORMULA:
				return cell.getCellFormula();
			case BLANK:
			default:
				return "";
		}
	}
}
