package com.parabank.testcases;

import java.lang.reflect.Method;
import java.util.List;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.parabank.pageobjects.RegistrationPage;
import com.parabank.testdata.RegistrationDataProvider;
import com.parabank.testdata.RegistrationTestData;
// import com.parabank.testutilities.ScreenshotsUtilities; // uncomment if you wire up screenshots below

/**
 * Test class for ParaBank customer registration.
 *
 * Assumes BaseClass (already in com.parabank.testcases) exposes a protected
 * WebDriver field named "driver" and handles browser launch / teardown using
 * the "browser" and "URL" parameters defined in testng.xml. See the
 * <test name="TC_parabank_registration"> block added to testng.xml.
 *
 * Adjust field/method names below if your BaseClass differs.
 */
public class TC_parabank_registration extends BaseClass {

 private RegistrationPage registrationPage;

 @BeforeMethod
 public void setUpPage() {
  registrationPage = new RegistrationPage(driver); // "driver" comes from BaseClass
  registrationPage.navigateTo("https://parabank.parasoft.com/parabank");
 }

 @Test(priority = 1, dataProvider = "validRegistrationData",
   dataProviderClass = RegistrationDataProvider.class,
   description = "Verify a new customer can register successfully with valid details")
 public void testSuccessfulRegistration(RegistrationTestData data) {
  registerFromData(data);

  Assert.assertTrue(registrationPage.isRegistrationSuccessful(),
    "Expected the welcome/success page to be displayed after registration.");
  Assert.assertTrue(registrationPage.getWelcomeText().contains(data.getUsername()),
    "Expected welcome message to contain the new username.");
  Assert.assertTrue(registrationPage.getSuccessMessageText().toLowerCase()
    .contains("your account was created successfully"),
    "Expected confirmation text about successful account creation.");
 }

 @Test(priority = 2, dataProvider = "invalidRegistrationData",
   dataProviderClass = RegistrationDataProvider.class,
   description = "Verify validation errors are shown for various invalid/incomplete submissions")
 public void testRegistrationValidationErrors(RegistrationTestData data, String expectedErrorSubstring) {
  registerFromData(data);

  List<String> errors = registrationPage.getFieldErrorMessages();
  Assert.assertFalse(errors.isEmpty(), "Expected at least one validation error message.");
  Assert.assertTrue(
    errors.stream().anyMatch(e -> e.toLowerCase().contains(expectedErrorSubstring)),
    "Expected a validation message containing '" + expectedErrorSubstring
      + "' but got: " + errors);
 }

 @Test(priority = 3, description = "Verify registering twice with the same username shows an already-exists error")
 public void testRegistrationWithDuplicateUsername() {
  RegistrationTestData duplicateUser = RegistrationTestData.defaultValidUser();

  // First registration should succeed
  registerFromData(duplicateUser);
  Assert.assertTrue(registrationPage.isRegistrationSuccessful(),
    "Pre-condition failed: first registration with this username did not succeed.");

  // Navigate back to the registration page and try the same username again
  registrationPage.navigateTo("https://parabank.parasoft.com/parabank");
  registerFromData(duplicateUser);

  Assert.assertTrue(registrationPage.isUsernameTakenErrorDisplayed(),
    "Expected a 'username already exists' validation error on duplicate registration.");
 }

 @AfterMethod
 public void tearDownTest(ITestResult result) {
  if (result.getStatus() == ITestResult.FAILURE) {
   // Plug in your existing screenshot utility here, e.g.:
   // ScreenshotsUtilities.captureScreenshot(driver, result.getName());
   System.out.println("Test failed: " + result.getName());
  }
 }

 /**
  * Helper: read common properties from RegistrationTestData (using reflection fallbacks)
  * and call the existing RegistrationPage.registerNewCustomer(...) that expects 11 strings.
  */
 private void registerFromData(RegistrationTestData data) {
  String firstName = getProperty(data, "firstName", "firstname", "FirstName");
  String lastName = getProperty(data, "lastName", "lastname", "LastName");
  String address = getProperty(data, "address", "streetAddress", "Address");
  String city = getProperty(data, "city", "City");
  String state = getProperty(data, "state", "State");
  String zipCode = getProperty(data, "zipCode", "zipcode", "zip", "ZipCode");
  String phone = getProperty(data, "phone", "phoneNumber", "telephone", "Phone");
  String ssn = getProperty(data, "ssn", "SSN", "Ssn");
  String username = getProperty(data, "username", "userName", "Username");
  String password = getProperty(data, "password", "pass", "Password");
  String confirmPassword = getProperty(data, "confirmPassword", "confirm", "confirmPass", "confirmPassword");

  registrationPage.registerNewCustomer(
    safeStr(firstName),
    safeStr(lastName),
    safeStr(address),
    safeStr(city),
    safeStr(state),
    safeStr(zipCode),
    safeStr(phone),
    safeStr(ssn),
    safeStr(username),
    safeStr(password),
    safeStr(confirmPassword)
  );
 }

 private String safeStr(String s) {
  return s == null ? "" : s;
 }

 private String getProperty(RegistrationTestData data, String... candidates) {
  Class<?> cls = data.getClass();
  for (String cand : candidates) {
   String[] methodNames = new String[] {
     "get" + capitalize(cand),
     "get" + cand,
     cand,
     "is" + capitalize(cand)
   };
   for (String mName : methodNames) {
    try {
     Method m = cls.getMethod(mName);
     Object val = m.invoke(data);
     if (val != null) return String.valueOf(val);
    } catch (NoSuchMethodException ignored) {
     // try next candidate
    } catch (Exception e) {
     // ignore and continue to other candidates
    }
   }
  }
  return "";
 }

 private String capitalize(String s) {
  if (s == null || s.isEmpty()) return s;
  if (s.length() == 1) return s.toUpperCase();
  return Character.toUpperCase(s.charAt(0)) + s.substring(1);
 }
}
