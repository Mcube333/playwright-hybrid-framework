package com.tests.web.furlenco;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import java.util.Set;
import org.testng.SkipException;
import org.testng.annotations.Test;

/** PDP layout (PDF: PDP layout). Sections verified live on stag.furlenco.com 2026-10-06. */
@Epic("Furlenco Web Automation")
@Feature("PDP Layout")
public class FurlencoPdpLayoutTest extends FurlencoCityStockSupport {

    @Test(groups = {"smoke", "web", "furlenco"}, priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify the rent PDP shows name, price, discount, delivery details and the information sections")
    public void verifyRentPdpLayout() {
        Set<String> inStock = stockPage.open(furlencoUrl, HOME_CITY, CATEGORY).getInStockProductPaths();
        if (inStock.isEmpty()) {
            throw new SkipException("No in-stock " + CATEGORY + " product in " + HOME_CITY);
        }

        pdpPage.open(furlencoUrl, inStock.iterator().next());

        assertSoftly(soft -> {
            soft.assertThat(pdpPage.getProductName()).as("Product name in the page title").isNotBlank();
            soft.assertThat(pdpPage.isPriceDisplayed()).as("Monthly price").isTrue();
            soft.assertThat(pdpPage.isDiscountBadgeDisplayed()).as("Discount badge").isTrue();
            soft.assertThat(pdpPage.isZeroDepositDisplayed()).as("Zero deposit line").isTrue();
            soft.assertThat(pdpPage.isAddToCartEnabled()).as("Add to cart enabled").isTrue();
            soft.assertThat(pdpPage.isDeliverySectionDisplayed()).as("Delivery & Assembly Details").isTrue();
            soft.assertThat(pdpPage.areInfoSectionsDisplayed()).as("Specifications, About, Care, FAQ").isTrue();
        });
        assertThat(pdpPage.getDeliveryEstimate()).startsWith("Delivery and Assemble by");
    }
}
