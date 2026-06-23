package com.parabank.testutilities;

import java.io.ByteArrayInputStream;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.parabank.testcases.BaseClass;
import io.qameta.allure.Allure;

public class AllureListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("Test Failed! Capturing screenshot for Allure...");
        
        // Retrieve driver from BaseClass
     // Replace line 19 with this:
        WebDriver driver = null;
        try {
            driver = (WebDriver) result.getTestContext().getAttribute("WebDriver");
            if (driver == null) {
                // Fallback: Get it directly from the test class instance field
                driver = (WebDriver) result.getTestClass().getRealClass().getDeclaredField("driver").get(result.getInstance());
            }
        } catch (Exception e) {
            System.out.println("Could not retrieve driver instance: " + e.getMessage());
        }

        
        if (driver != null) {
            try {
                // Capture byte array for Allure report
                byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                
                // Explicitly attach to Allure execution tree
                Allure.addAttachment(
                    "Failure_Screenshot_" + result.getName(), 
                    new ByteArrayInputStream(screenshotBytes)
                );
            } catch (Exception e) {
                System.out.println("Exception while taking Allure screenshot: " + e.getMessage());
            }
        }
    }
}
