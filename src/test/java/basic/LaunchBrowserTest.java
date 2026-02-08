package basic;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;


public class LaunchBrowserTest {

   
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {


            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

            BrowserContext context  = browser.newContext();
            Page page = context.newPage();
            page.navigate("https://www.google.com");
            System.out.println(".(page.title())");

            page.close();
            context.close();
            browser.close();
        }
    }
}