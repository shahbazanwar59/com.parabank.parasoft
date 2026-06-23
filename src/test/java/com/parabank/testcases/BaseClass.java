package com.parabank.testcases;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

public class BaseClass {

    public WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser","URL"})
    public void setUp(String browser, String URL) {

        if(browser.equalsIgnoreCase("chrome")) {
            driver = new ChromeDriver();
        }

        driver.manage().window().maximize();
        driver.get(URL);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {

        if(driver != null) {
            driver.quit();
        }
    }
}