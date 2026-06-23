package com.parabank.pageobjects;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object for the ParaBank "Register for Free Online Account Access" page.
 * URL: https://parabank.parasoft.com/parabank/register.htm
 */
public class RegistrationPage {

	private WebDriver driver;
	private WebDriverWait wait;

	// ---------------- Locators ----------------

	@FindBy(id = "customer.firstName")
	private WebElement firstNameInput;

	@FindBy(id = "customer.lastName")
	private WebElement lastNameInput;

	@FindBy(id = "customer.address.street")
	private WebElement addressInput;

	@FindBy(id = "customer.address.cit")
	private WebElement cityInput;

	@FindBy(id = "customer.address.state")
	private WebElement stateInput;

	@FindBy(id = "customer.address.zipCode")
	private WebElement zipCodeInput;

	@FindBy(id = "customer.phoneNumber")
	private WebElement phoneNumberInput;

	@FindBy(id = "customer.ssn")
	private WebElement ssnInput;

	@FindBy(id = "customer.username")
	private WebElement usernameInput;

	@FindBy(id = "customer.password")
	private WebElement passwordInput;

	@FindBy(id = "repeatedPassword")
	private WebElement confirmPasswordInput;

	@FindBy(xpath = "//input[@value='Register']")
	private WebElement registerButton;

	// Shown on successful registration: <div id="rightPanel"><h1>Welcome ...</h1>
	@FindBy(xpath = "//div[@id='rightPanel']/h1")
	private WebElement welcomeHeading;

	@FindBy(xpath = "//div[@id='rightPanel']/p")
	private WebElement successMessage;

	// Field-level validation errors, e.g. <span class="error">First name is required.</span>
	@FindBy(className = "error")
	private List<WebElement> fieldErrors;

	// ---------------- Constructor ----------------

	public RegistrationPage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		PageFactory.initElements(driver, this);
	}

	// ---------------- Navigation ----------------

	public void navigateTo(String url) {
		driver.get(url + (url.endsWith("/") ? "register.htm" : "/register.htm"));
		wait.until(ExpectedConditions.visibilityOf(firstNameInput));
	}

	// ---------------- Actions ----------------

	public RegistrationPage enterFirstName(String firstName) {
		wait.until(ExpectedConditions.visibilityOf(firstNameInput)).clear();
		firstNameInput.sendKeys(firstName);
		return this;
	}

	public RegistrationPage enterLastName(String lastName) {
		lastNameInput.clear();
		lastNameInput.sendKeys(lastName);
		return this;
	}

	public RegistrationPage enterAddress(String address) {
		addressInput.clear();
		addressInput.sendKeys(address);
		return this;
	}

	public RegistrationPage enterCity(String city) {
		cityInput.clear();
		cityInput.sendKeys(city);
		return this;
	}

	public RegistrationPage enterState(String state) {
		stateInput.clear();
		stateInput.sendKeys(state);
		return this;
	}

	public RegistrationPage enterZipCode(String zipCode) {
		zipCodeInput.clear();
		zipCodeInput.sendKeys(zipCode);
		return this;
	}

	public RegistrationPage enterPhoneNumber(String phoneNumber) {
		phoneNumberInput.clear();
		phoneNumberInput.sendKeys(phoneNumber);
		return this;
	}

	public RegistrationPage enterSsn(String ssn) {
		ssnInput.clear();
		ssnInput.sendKeys(ssn);
		return this;
	}

	public RegistrationPage enterUsername(String username) {
		usernameInput.clear();
		usernameInput.sendKeys(username);
		return this;
	}

	public RegistrationPage enterPassword(String password) {
		passwordInput.clear();
		passwordInput.sendKeys(password);
		return this;
	}

	public RegistrationPage enterConfirmPassword(String confirmPassword) {
		confirmPasswordInput.clear();
		confirmPasswordInput.sendKeys(confirmPassword);
		return this;
	}

	public void clickRegister() {
		registerButton.click();
	}

	/**
	 * Convenience method to fill out and submit the entire registration form in one call.
	 */
	public void registerNewCustomer(String firstName, String lastName, String address, String city,
			String state, String zipCode, String phoneNumber, String ssn, String username,
			String password, String confirmPassword) {
		enterFirstName(firstName);
		enterLastName(lastName);
		enterAddress(address);
		enterCity(city);
		enterState(state);
		enterZipCode(zipCode);
		enterPhoneNumber(phoneNumber);
		enterSsn(ssn);
		enterUsername(username);
		enterPassword(password);
		enterConfirmPassword(confirmPassword);
		clickRegister();
	}

	// ---------------- Result / validation checks ----------------

	public boolean isRegistrationSuccessful() {
		try {
			wait.until(ExpectedConditions.visibilityOf(welcomeHeading));
			return welcomeHeading.getText().trim().startsWith("Welcome");
		} catch (Exception e) {
			return false;
		}
	}

	public String getWelcomeText() {
		return welcomeHeading.getText().trim();
	}

	public String getSuccessMessageText() {
		return successMessage.getText().trim();
	}

	/** Returns all visible inline field-validation error messages (e.g. "First name is required."). */
	public List<String> getFieldErrorMessages() {
		return fieldErrors.stream().map(WebElement::getText).filter(t -> !t.isEmpty()).toList();
	}

	/** True if the page is showing the "This username already exists." error. */
	public boolean isUsernameTakenErrorDisplayed() {
		return getFieldErrorMessages().stream()
				.anyMatch(msg -> msg.toLowerCase().contains("username is already taken")
						|| msg.toLowerCase().contains("already exists"));
	}
}
