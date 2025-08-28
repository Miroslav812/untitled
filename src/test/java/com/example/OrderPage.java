package com.example;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class OrderPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By NAME_FIELD = By.xpath("//input[@placeholder='* Имя']");
    private static final By SURNAME_FIELD = By.xpath("//input[@placeholder='* Фамилия']");
    private static final By ADDRESS_FIELD = By.xpath("//input[@placeholder='* Адрес: куда привезти заказ']");
    private static final By METRO_FIELD = By.xpath("//input[@placeholder='* Станция метро']");
    private static final By PHONE_FIELD = By.xpath("//input[@placeholder='* Телефон: на него позвонит курьер']");
    private static final By NEXT_BUTTON = By.xpath("//button[text()='Далее']");

    private static final By DATE_FIELD = By.xpath("//input[@placeholder='* Когда привезти самокат']");
    private static final By RENTAL_PERIOD_DROPDOWN = By.className("Dropdown-placeholder");
    private static final By COLOR_BLACK = By.id("black");
    private static final By COLOR_GREY = By.id("grey");
    private static final By COMMENT_FIELD = By.xpath("//input[@placeholder='Комментарий для курьера']");
    private static final By ORDER_BUTTON = By.xpath("//button[text()='Заказать' and contains(@class, 'Button_Middle__1CSJM')]");
    private static final By CONFIRM_BUTTON = By.xpath("//button[text()='Да']");
    private static final By SUCCESS_MODAL = By.className("Order_ModalHeader__3FDaJ");

    public OrderPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void fillFirstForm(String name, String surname, String address, String metro, String phone) {
        waitAndType(NAME_FIELD, name);
        waitAndType(SURNAME_FIELD, surname);
        waitAndType(ADDRESS_FIELD, address);

        WebElement metroInput = wait.until(ExpectedConditions.elementToBeClickable(METRO_FIELD));
        metroInput.sendKeys(metro);
        metroInput.sendKeys(Keys.ARROW_DOWN);
        metroInput.sendKeys(Keys.ENTER);

        waitAndType(PHONE_FIELD, phone);
        waitAndClick(NEXT_BUTTON);

        // ожидание второй формы
        wait.until(ExpectedConditions.visibilityOfElementLocated(DATE_FIELD));
    }

    public void fillSecondForm(String date, String period, String color, String comment) {
        waitAndType(DATE_FIELD, date);
        driver.findElement(DATE_FIELD).sendKeys(Keys.ENTER);

        waitAndClick(RENTAL_PERIOD_DROPDOWN);
        By periodOption = By.xpath(
                String.format("//div[contains(@class, 'Dropdown-menu')]//*[contains(text(), '%s')]", period)
        );
        wait.until(ExpectedConditions.elementToBeClickable(periodOption)).click();

        selectColor(color);
        waitAndType(COMMENT_FIELD, comment);
    }

    public void makeOrder() {
        waitAndClick(ORDER_BUTTON);
        waitAndClick(CONFIRM_BUTTON);
    }

    public boolean isOrderSuccess() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_MODAL)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private void selectColor(String color) {
        if (color == null) return;
        switch (color.toLowerCase()) {
            case "black":
                waitAndClick(COLOR_BLACK);
                break;
            case "grey":
                waitAndClick(COLOR_GREY);
                break;
        }
    }

    private void waitAndClick(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private void waitAndType(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
    }
}
