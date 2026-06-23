package com.parabank.testcases;

import java.io.ByteArrayInputStream;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import com.parabank.testutilities.ScreenshotsUtilities;
import io.qameta.allure.Allure; // Required Import

public class BaseClass {
	
	public WebDriver driver;

    @BeforeMethod
    @Parameters({"browser","URL"})
    public void setUp(String browser, String URL) {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(URL);
    }
    
    @AfterMethod
    public void tearDown(ITestResult result) {
        if (driver != null) {
          //  driver.quit(); // Ensure browser quits cleanly after test completes
        }
    }
}
