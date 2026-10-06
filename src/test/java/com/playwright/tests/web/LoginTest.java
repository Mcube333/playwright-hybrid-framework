package com.playwright.tests.web;

import com.playwright.framework.config.Config;
import com.playwright.framework.pages.InventoryPage;
import com.playwright.framework.pages.LoginPage;
import com.playwright.tests.base.BaseWebTest;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends BaseWebTest {

    @Test(groups = {"smoke", "web", "public"})
    public void validUserCanLogin() {
        InventoryPage inventory = new LoginPage(page).open()
                .loginAs(Config.get("web.username"), Config.get("web.password"));

        assertThat(inventory.isLoaded()).isTrue();
        assertThat(inventory.productCount()).isGreaterThan(0);
    }

    @Test(groups = {"regression", "web", "public"})
    public void lockedOutUserSeesError() {
        String error = new LoginPage(page).open()
                .attemptLogin("locked_out_user", Config.get("web.password"))
                .errorMessage();

        assertThat(error).contains("locked out");
    }

    @Test(groups = {"regression", "web", "public"})
    public void userCanAddItemToCart() {
        InventoryPage inventory = new LoginPage(page).open()
                .loginAs(Config.get("web.username"), Config.get("web.password"))
                .addToCart("Sauce Labs Backpack");

        assertThat(inventory.cartBadgeCount()).isEqualTo("1");
    }
}
