package com.parabank.testcases;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext; // Added import
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

public class BaseClass {
	
    public WebDriver driver;

    @BeforeMethod
    @Parameters({"browser","URL"})
    public void setUp(String browser, String URL, ITestContext context) { // Added ITestContext parameter
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(URL);
        
        // Save the driver instance inside the TestNG Context for the listener to fetch cleanly
        context.setAttribute("WebDriver", driver);
    }
    
    @AfterMethod
    public void tearDown(ITestResult result) {
        if (driver != null) {
            driver.quit(); 
        }
    }
}
