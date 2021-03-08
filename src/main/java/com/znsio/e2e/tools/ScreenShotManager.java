package com.znsio.e2e.tools;

import com.context.SessionContext;
import com.context.TestExecutionContext;
import com.epam.reportportal.service.ReportPortal;
import com.znsio.e2e.entities.TEST_CONTEXT;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.util.Date;


public class ScreenShotManager {

    private final TestExecutionContext context;
    private final String directoryPath;
    private int counter;

    public ScreenShotManager () {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        directoryPath = context.getTestStateAsString(TEST_CONTEXT.SCREENSHOT_DIRECTORY);
        counter = 0;
        File file = new File(directoryPath);
        file.getParentFile().mkdirs();
    }

    public void takeScreenShot (String fileName) {
        Driver driver = (Driver) context.getTestState(TEST_CONTEXT.CURRENT_DRIVER);
        if (null != driver) {
            File screenshot = ((TakesScreenshot) driver.getInnerDriver()).getScreenshotAs(OutputType.FILE);
            fileName = normaliseScenarioName(getPrefix() + "-" + fileName);
            File destinationFile = createScreenshotFile(directoryPath, fileName);
            System.out.println("The screenshot is placed in : " + destinationFile.getAbsolutePath());
            try {
                FileUtils.copyFile(screenshot, destinationFile);
                ReportPortal.emitLog(fileName, "DEBUG", new Date(), destinationFile);
            } catch (IOException e) {
                System.out.printf("ERROR: Unable to save or upload screenshot: '%s' or upload sceeenshot to ReportPortal%n", destinationFile.getAbsolutePath());
                e.printStackTrace();
            }
        } else {
            System.out.println("Driver is not instantiated for this test");
        }
    }

    private int getPrefix () {
        return ++counter;
    }

    private String normaliseScenarioName (String scenarioName) {
        return scenarioName.replaceAll("[`~ !@#$%^&*()\\-=+\\[\\]{}\\\\|;:'\",<.>/?]", "_");
    }

    private File createScreenshotFile (String dirName, String fileName) {
        fileName = fileName.endsWith(".png") ? fileName : fileName + ".png";
        return new File(System.getProperty("user.dir")
                + dirName
                + File.separator
                + fileName);
    }
}