package com.parabank.testutilities;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;
import io.qameta.allure.Allure;

public class AllureListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("Test Failed! Capturing screenshot for Allure...");
        
        WebDriver driver = null;
        try {
            // Check test context first
            driver = (WebDriver) result.getTestContext().getAttribute("WebDriver");
            
            if (driver == null) {
                Object testInstance = result.getInstance();
                Class<?> currentClass = testInstance.getClass();
                
                // Walk up the class hierarchy to locate the driver field in BaseClass
                while (currentClass != null) {
                    try {
                        Field field = currentClass.getDeclaredField("driver");
                        field.setAccessible(true);
                        driver = (WebDriver) field.get(testInstance);
                        break; 
                    } catch (NoSuchFieldException e) {
                        currentClass = currentClass.getSuperclass(); // Check parent class
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Could not retrieve driver instance: " + e.getMessage());
        }

        if (driver != null) {
            try {
                byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment(
                    "Failure_Screenshot_" + result.getName(), 
                    new ByteArrayInputStream(screenshotBytes)
                );
                System.out.println("Screenshot successfully attached to Allure report!");
            } catch (Exception e) {
                System.out.println("Exception while taking Allure screenshot: " + e.getMessage());
            }
        } else {
            System.out.println("Driver was null! Cannot attach screenshot.");
        }
    }
}
