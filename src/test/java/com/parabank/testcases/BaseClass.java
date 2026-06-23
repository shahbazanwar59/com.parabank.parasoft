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
	
	WebDriver driver;

    @BeforeMethod
    @Parameters({"browser","URL"})
    public void setUp(String browser, String URL) {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(URL);
    }
    
    @AfterMethod
    public void tearDown(ITestResult result) {
        try {
            if (ITestResult.FAILURE == result.getStatus()) {
                String screenshotPath = "Screenshots/" + result.getName() + "_" + ScreenshotsUtilities.getTimestamp() + ".jpg"; 
                if (driver != null) {
                    // 1. Keep your existing local file save logic
                    ScreenshotsUtilities.takeScreenshot(driver, screenshotPath);
                    
                    // 2. Add this block to send the screenshot byte array to Allure
                    byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    Allure.addAttachment(
                        "Failure Screenshot - " + result.getName(), 
                        new ByteArrayInputStream(screenshotBytes)
                    );
                }
               
                System.out.println("Screenshot saved locally and attached to Allure: " + screenshotPath);
            }
        }
        catch (Exception e) {
            System.out.println("Error in teardown: " + e.getMessage());
        }
        // driver.quit(); // Keep or un-comment based on your parallel testing requirements
    }
}
