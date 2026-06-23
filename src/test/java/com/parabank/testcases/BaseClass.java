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

import io.qameta.allure.Allure;

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
        try {
            // Check if the TestNG method status points to a Failure
            if (result.getStatus() == ITestResult.FAILURE) {
                
                if (driver != null) {
                    // 1. Manually force Allure to record the actual failure status and message
                    Allure.getLifecycle().updateTestCase(testResult -> {
                        testResult.setStatus(io.qameta.allure.model.Status.FAILED);
                        if (result.getThrowable() != null) {
                            testResult.setStatusDetails(new io.qameta.allure.model.StatusDetails()
                                .setMessage(result.getThrowable().getMessage()));
                        }
                    });

                    // 2. Capture and immediately attach the screenshot to the active test context
                    byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    Allure.addAttachment(
                        "Teardown_Failure_Screenshot_" + result.getName(), 
                        "image/png", 
                        new ByteArrayInputStream(screenshotBytes), 
                        "png"
                    );
                    
                    System.out.println("Screenshot successfully integrated into Allure via Lifecycle API.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error capturing screenshot in teardown: " + e.getMessage());
        } finally {
            // 3. Close down the browser context
            if (driver != null) {
                driver.quit(); 
            }
        }
    }
}
