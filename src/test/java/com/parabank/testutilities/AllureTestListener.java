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

public class AllureTestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {

        attachException(result);
        attachScreenshot(result);
    }

    private void attachException(ITestResult result) {

        if(result.getThrowable() != null) {

            Allure.addAttachment(
                    "Failure Exception",
                    result.getThrowable().toString());
        }
    }

    private void attachScreenshot(ITestResult result) {

        try {

            Object testInstance = result.getInstance();

            if(testInstance == null) {
                return;
            }

            Field driverField = findDriverField(testInstance.getClass());

            if(driverField == null) {
                return;
            }

            driverField.setAccessible(true);

            WebDriver driver =
                    (WebDriver) driverField.get(testInstance);

            if(driver == null) {
                return;
            }

            byte[] screenshot =
                    ((TakesScreenshot) driver)
                            .getScreenshotAs(OutputType.BYTES);

            Allure.addAttachment(
                    "Failure Screenshot",
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    ".png");

        } catch (Exception e) {

            Allure.addAttachment(
                    "Screenshot Error",
                    e.getMessage());
        }
    }

    private Field findDriverField(Class<?> clazz) {

        while(clazz != null) {

            try {

                return clazz.getDeclaredField("driver");

            } catch (NoSuchFieldException e) {

                clazz = clazz.getSuperclass();
            }
        }

        return null;
    }

    @Override
    public void onStart(ITestContext context) {}

    @Override
    public void onFinish(ITestContext context) {}

    @Override
    public void onTestStart(ITestResult result) {}

    @Override
    public void onTestSuccess(ITestResult result) {}

    @Override
    public void onTestSkipped(ITestResult result) {}

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}
}