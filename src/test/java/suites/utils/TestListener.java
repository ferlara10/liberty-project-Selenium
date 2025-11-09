package suites.utils;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.codeborne.selenide.Selenide;
import org.openqa.selenium.OutputType;
import org.testng.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestListener implements ITestListener {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        ExtentSparkReporter spark = new ExtentSparkReporter("extent-report.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().pass("Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().fail(result.getThrowable());

        try {
            String screenshotPath = "screenshots/" + result.getMethod().getMethodName() + ".png";
            File screenshotFile = Selenide.screenshot(OutputType.FILE);  // Capture screenshot

            if (screenshotFile != null) {
                Files.createDirectories(Paths.get("screenshots")); // Ensure directory exists
                Files.copy(screenshotFile.toPath(), Paths.get(screenshotPath));
                test.get().addScreenCaptureFromPath(screenshotPath);  // Attach to report
            }
        } catch (Exception e) {
            test.get().warning("Failed to capture screenshot: " + e.getMessage());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}

