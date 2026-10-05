package com.playwright.tests.base;

import com.microsoft.playwright.Page;
import com.playwright.framework.core.PlaywrightManager;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseWebTest {
    protected Page page;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        page = PlaywrightManager.startWeb();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        PlaywrightManager.stopWeb(result.getMethod().getMethodName(), !result.isSuccess());
    }
}
