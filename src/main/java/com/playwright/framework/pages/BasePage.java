package com.playwright.framework.pages;

import com.microsoft.playwright.Page;

public abstract class BasePage {
    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    public String title() {
        return page.title();
    }

    public String currentUrl() {
        return page.url();
    }
}
