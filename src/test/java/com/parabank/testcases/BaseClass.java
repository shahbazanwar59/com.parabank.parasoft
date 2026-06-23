package com.parabank.testcases;

import java.io.ByteArrayInputStream;
import java.util.UUID;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import com.parabank.testutilities.ScreenshotsUtilities;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;

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
            // Check if TestNG marked the test as a failure
            if (result.getStatus() == ITestResult.FAILURE) {
                
                // 1. Force Allure to register the failure explicitly by UUID context
                String currentTestUuid = Allure.getLifecycle().getCurrentTestCase().orElse(UUID.randomUUID().toString());
                
                Allure.getLifecycle().updateTestCase(currentTestUuid, testCase -> {
                    testCase.setStatus(Status.FAILED); // Forces report metric to turn RED
                    
                    if (result.getThrowable() != null) {
                        StatusDetails details = new StatusDetails()
                            .setMessage(result.getThrowable().getMessage())
                            .setTrace(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(result.getThrowable()));
                        testCase.setStatusDetails(details);
                    }
                });

                if (driver != null) {
                    // 2. Save file locally as you requested
                    String screenshotPath = "Screenshots/" + result.getName() + "_" + ScreenshotsUtilities.getTimestamp() + ".jpg"; 
                    ScreenshotsUtilities.takeScreenshot(driver, screenshotPath);
                    System.out.println("Screenshot saved locally: " + screenshotPath);
                    
                    // 3. Attach directly to Allure so it displays inside the teardown phase
                    byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    Allure.getLifecycle().addAttachment(
                        "Failure_Screenshot_" + result.getName(), 
                        "image/png", 
                        "png", 
                        new ByteArrayInputStream(screenshotBytes)
                    );
                    System.out.println("Screenshot byte array injected into Allure Context.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error in teardown processing: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit(); 
            }
        }
    }
}
