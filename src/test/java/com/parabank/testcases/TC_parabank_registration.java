package com.parabank.testcases;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.parabank.pageobjects.PO_RegistrationPage;
// import com.parabank.testutilities.ScreenshotsUtilities; // uncomment if you wire up screenshots below

/**
 * Test class for ParaBank customer registration.
 *
 * Assumes BaseClass (already in com.parabank.testcases) exposes a protected
 * WebDriver field named "driver" and handles browser launch / teardown using
 * the "browser" and "URL" parameters defined in testng.xml, e.g.:
 *
 *   <test name="TC_parabank_registration">
 *     <parameter name="browser" value="Chrome"/>
 *     <parameter name="URL" value="https://parabank.parasoft.com/parabank/index.htm"/>
 *     <classes>
 *       <class name="com.parabank.testcases.TC_parabank_registration"/>
 *     </classes>
 *   </test>
 *
 * Adjust field/method names below if your BaseClass differs.
 */
public class TC_parabank_registration extends BaseClass {

	private PO_RegistrationPage registrationPage;
	private String generatedUsername;

	@BeforeMethod
	public void setUpPage() {
		registrationPage = new PO_RegistrationPage(driver); // "driver" comes from BaseClass
		registrationPage.navigateTo(driver.getCurrentUrl().contains("parabank")
				? "https://parabank.parasoft.com/parabank"
				: driver.getCurrentUrl());
	}

	@Test(priority = 1, description = "Verify a new customer can register successfully with valid details")
	public void testSuccessfulRegistration() {
		generatedUsername = "testuser" + System.currentTimeMillis();

		registrationPage.registerNewCustomer(
				"John",                 // first name
				"Smith",                // last name
				"123 Main Street",      // address
				"Springfield",          // city
				"IL",                   // state
				"62704",                // zip code
				"5551234567",           // phone number
				"123456789",            // ssn
				generatedUsername,      // username
				"Passw0rd!",            // password
				"Passw0rd!"             // confirm password
		);

		Assert.assertTrue(registrationPage.isRegistrationSuccessful(),
				"Expected the welcome/success page to be displayed after registration.");
		Assert.assertTrue(registrationPage.getWelcomeText().contains(generatedUsername),
				"Expected welcome message to contain the new username.");
		Assert.assertTrue(registrationPage.getSuccessMessageText().toLowerCase()
				.contains("your account was created successfully"),
				"Expected confirmation text about successful account creation.");
	}

	@Test(priority = 2, description = "Verify validation errors are shown when all mandatory fields are left blank")
	public void testRegistrationWithBlankMandatoryFields() {
		registrationPage.clickRegister();

		List<String> errors = registrationPage.getFieldErrorMessages();
		Assert.assertFalse(errors.isEmpty(),
				"Expected validation error messages when submitting an empty registration form.");
		Assert.assertTrue(errors.stream().anyMatch(e -> e.toLowerCase().contains("first name")),
				"Expected a 'First name is required' style validation message.");
	}

	@Test(priority = 3, description = "Verify registering twice with the same username shows an already-exists error")
	public void testRegistrationWithDuplicateUsername() {
		String duplicateUsername = "dupeuser" + System.currentTimeMillis();

		// First registration should succeed
		registrationPage.registerNewCustomer(
				"Jane", "Doe", "456 Oak Avenue", "Metropolis", "NY", "10001",
				"5559876543", "987654321", duplicateUsername, "Passw0rd!", "Passw0rd!");
		Assert.assertTrue(registrationPage.isRegistrationSuccessful(),
				"Pre-condition failed: first registration with this username did not succeed.");

		// Navigate back to the registration page and try the same username again
		registrationPage.navigateTo("https://parabank.parasoft.com/parabank");
		registrationPage.registerNewCustomer(
				"Jane", "Doe", "456 Oak Avenue", "Metropolis", "NY", "10001",
				"5559876543", "987654321", duplicateUsername, "Passw0rd!", "Passw0rd!");

		Assert.assertTrue(registrationPage.isUsernameTakenErrorDisplayed(),
				"Expected a 'username already exists' validation error on duplicate registration.");
	}

	/*
	 * @AfterMethod public void tearDownTest(ITestResult result) { if
	 * (result.getStatus() == ITestResult.FAILURE) { // Plug in your existing
	 * screenshot utility here, e.g.: //
	 * ScreenshotsUtilities.captureScreenshot(driver, result.getName());
	 * System.out.println("Test failed: " + result.getName()); } }
	 */
}
