package com.playwright.framework.core;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.playwright.framework.config.Config;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Thread-safe Playwright lifecycle. Each test thread owns its own Playwright, Browser,
 * BrowserContext and Page, so tests can run in parallel.
 */
public final class PlaywrightManager {
    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    private PlaywrightManager() {}

    public static Page startWeb() {
        Playwright pw = Playwright.create();
        PLAYWRIGHT.set(pw);

        BrowserType type = switch (Config.browser().toLowerCase()) {
            case "firefox" -> pw.firefox();
            case "webkit" -> pw.webkit();
            default -> pw.chromium();
        };
        Browser browser = type.launch(new BrowserType.LaunchOptions()
                .setHeadless(Config.headless())
                .setSlowMo(Config.slowMo()));
        BROWSER.set(browser);

        BrowserContext ctx = browser.newContext(new Browser.NewContextOptions()
                .setBaseURL(Config.webBaseUrl())
                .setViewportSize(1920, 1080));
        ctx.setDefaultTimeout(Config.timeoutMs());
        ctx.tracing().start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(true));
        CONTEXT.set(ctx);

        Page page = ctx.newPage();
        PAGE.set(page);
        return page;
    }

    public static Page page() {
        Page p = PAGE.get();
        if (p == null) throw new IllegalStateException("No Page for this thread; was startWeb() called?");
        return p;
    }

    /** Full-page screenshot, or null when this thread has no web page (e.g. an API test). */
    public static byte[] screenshot() {
        Page p = PAGE.get();
        return p == null ? null : p.screenshot(new Page.ScreenshotOptions().setFullPage(true));
    }

    /** Saves the trace only when {@code keepTrace} is true, then closes everything. */
    public static void stopWeb(String testName, boolean keepTrace) {
        try {
            BrowserContext ctx = CONTEXT.get();
            if (ctx != null) {
                if (keepTrace) {
                    Path trace = Paths.get(Config.artifactsDir(), "traces", testName + ".zip");
                    ctx.tracing().stop(new Tracing.StopOptions().setPath(trace));
                } else {
                    ctx.tracing().stop();
                }
                ctx.close();
            }
            if (BROWSER.get() != null) BROWSER.get().close();
            if (PLAYWRIGHT.get() != null) PLAYWRIGHT.get().close();
        } finally {
            PAGE.remove();
            CONTEXT.remove();
            BROWSER.remove();
            PLAYWRIGHT.remove();
        }
    }
}
