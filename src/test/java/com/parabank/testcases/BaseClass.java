package com.parabank.testcases;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import com.parabank.testutilities.ScreenshotsUtilities;

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
                   ScreenshotsUtilities.takeScreenshot(driver, screenshotPath);
               }
               
               System.out.println("Screenshot saved: " + screenshotPath);
		
      
           	}
		}
		catch (Exception e) {
			System.out.println("Error in teardown" +e.getMessage());
		}
		 //driver.quit();
	}
   
	
}

