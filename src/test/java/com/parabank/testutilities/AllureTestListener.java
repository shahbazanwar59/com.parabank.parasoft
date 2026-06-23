package com.parabank.testutilities;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import io.qameta.allure.Allure;

/**
 * Attaches failure information and a screenshot to Allure results on test failure.
 * It attempts to find a WebDriver field named 'driver' on the test instance via reflection.
 */
public class AllureTestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        // Attach exception stacktrace
        Throwable t = result.getThrowable();
        if (t != null) {
            Allure.addAttachment("failure-exception", t.toString());
        }

        // Try to find a WebDriver field on the test instance
        Object testInstance = result.getInstance();
        if (testInstance != null) {
            try {
                Field driverField = findDriverField(testInstance.getClass());
                if (driverField != null) {
                    driverField.setAccessible(true);
                    Object drv = driverField.get(testInstance);
                    if (drv instanceof WebDriver) {
                        WebDriver driver = (WebDriver) drv;
                        try {
                            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                            if (screenshot != null && screenshot.length > 0) {
                                Allure.addAttachment("screenshot-on-failure", new ByteArrayInputStream(screenshot));
                            }
                        } catch (Exception e) {
                            Allure.addAttachment("screenshot-error", e.toString());
                        }
                    }
                }
            } catch (Exception e) {
                Allure.addAttachment("driver-reflection-error", e.toString());
            }
        }
    }

    private Field findDriverField(Class<?> cls) {
        while (cls != null && cls != Object.class) {
            try {
                // common field names: driver, webDriver
                for (String name : new String[] { "driver", "webDriver" }) {
                    try {
                        Field f = cls.getDeclaredField(name);
                        return f;
                    } catch (NoSuchFieldException ignored) { }
                }
            } finally {
                cls = cls.getSuperclass();
            }
        }
        return null;
    }

    // no-op implementations for other listener methods
    @Override public void onTestStart(ITestResult result) {}
    @Override public void onTestSuccess(ITestResult result) {}
    @Override public void onTestSkipped(ITestResult result) {}
    @Override public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}
    @Override public void onStart(ITestContext context) {}
    @Override public void onFinish(ITestContext context) {}
}
