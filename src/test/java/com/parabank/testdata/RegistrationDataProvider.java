package com.parabank.testdata;

import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;

import com.parabank.testutilities.ExcelUtils;

/**
 * Centralized TestNG data providers for registration tests, backed by an Excel workbook.
 *
 * Expected workbook location: src/test/resources/testdata/RegistrationTestData.xlsx
 * (placed under src/test/resources so Maven copies it onto the test classpath).
 *
 *   Sheet "ValidData":   FirstName | LastName | Address | City | State | ZipCode |
 *                        PhoneNumber | SSN | Username | Password | ConfirmPassword
 *
 *   Sheet "InvalidData": same columns + ExpectedError (substring expected in the
 *                        validation message shown on the page)
 *
 * Reference from a test method with:
 *   @Test(dataProvider = "validRegistrationData", dataProviderClass = RegistrationDataProvider.class)
 */
public class RegistrationDataProvider {

	// Classpath-relative path (resolved via src/test/resources). Falls back to a plain
	// file path if not found on the classpath - see ExcelUtils.resolveInputStream.
	private static final String EXCEL_RESOURCE = "testdata/RegistrationTestData.xlsx";

	@DataProvider(name = "validRegistrationData")
	public static Object[][] validRegistrationData() {
		List<Map<String, String>> rows = ExcelUtils.readSheetAsMaps(EXCEL_RESOURCE, "ValidData");
		Object[][] data = new Object[rows.size()][1];
		long ts = System.currentTimeMillis();
		for (int i = 0; i < rows.size(); i++) {
			data[i][0] = mapRowToTestData(rows.get(i), ts + i);
		}
		return data;
	}

	@DataProvider(name = "invalidRegistrationData")
	public static Object[][] invalidRegistrationData() {
		List<Map<String, String>> rows = ExcelUtils.readSheetAsMaps(EXCEL_RESOURCE, "InvalidData");
		Object[][] data = new Object[rows.size()][2];
		long ts = System.currentTimeMillis();
		for (int i = 0; i < rows.size(); i++) {
			Map<String, String> row = rows.get(i);
			data[i][0] = mapRowToTestData(row, ts + i);
			data[i][1] = row.get("ExpectedError");
		}
		return data;
	}

	private static RegistrationTestData mapRowToTestData(Map<String, String> row, long uniqueSuffix) {
		String username = row.get("Username");
		// Append a unique suffix so re-runs don't collide on "username already exists",
		// unless the row intentionally leaves username blank (a validation test case).
		if (username != null && !username.isEmpty()) {
			username = username + uniqueSuffix;
		}
		return RegistrationTestData.builder()
				.firstName(row.get("FirstName"))
				.lastName(row.get("LastName"))
				.address(row.get("Address"))
				.city(row.get("City"))
				.state(row.get("State"))
				.zipCode(row.get("ZipCode"))
				.phoneNumber(row.get("PhoneNumber"))
				.ssn(row.get("SSN"))
				.username(username)
				.password(row.get("Password"))
				.confirmPassword(row.get("ConfirmPassword"))
				.build();
	}
}
