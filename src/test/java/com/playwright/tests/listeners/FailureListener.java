package com.playwright.tests.listeners;

import com.playwright.framework.config.Config;
import com.playwright.framework.core.PlaywrightManager;
import io.qameta.allure.Allure;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Saves a full-page screenshot for failed web tests and attaches it to the Allure report.
 * Registered via META-INF/services, so it applies to every run. Runs before @AfterMethod closes the browser.
 */
public class FailureListener implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result) {
        try {
            byte[] png = PlaywrightManager.screenshot();
            if (png == null) return; // API test, no page
            Path dir = Paths.get(Config.artifactsDir(), "screenshots");
            Files.createDirectories(dir);
            Files.write(dir.resolve(result.getMethod().getMethodName() + ".png"), png);
            Allure.addAttachment("Failure screenshot", "image/png", new ByteArrayInputStream(png), "png");
        } catch (Exception e) {
            System.err.println("Could not capture screenshot: " + e.getMessage());
        }
    }
}
