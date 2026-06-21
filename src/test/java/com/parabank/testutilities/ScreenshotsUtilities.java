package com.parabank.testutilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotsUtilities {
	public static void takeScreenshot(WebDriver driver, String fileName) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(source, new File("./Screenshots/" + fileName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getTimestamp() {
        return new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date());
    }

}
