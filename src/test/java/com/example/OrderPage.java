package com.example;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By NAME = By.xpath("//input[@placeholder='* Имя']");
    private static final By SURNAME = By.xpath("//input[@placeholder='* Фамилия']");
    private static final By ADDRESS = By.xpath("//input[@placeholder='* Адрес: куда привезти заказ']");
    private static final By METRO = By.xpath("//input[@placeholder='* Станция метро']");
    private static final By METRO_OPTIONS = By.cssSelector(".select-search__select .select-search__option");
    private static final By PHONE = By.xpath("//input[@placeholder='* Телефон: на него позвонит курьер']");
    private static final By NEXT = By.xpath("//button[normalize-space()='Далее']");

    private static final By DATE = By.xpath("//input[@placeholder='* Когда привезти самокат']");
    private static final By RENTAL_DROPDOWN = By.cssSelector(".Dropdown-placeholder");
    private static final By RENTAL_OPTIONS = By.cssSelector(".Dropdown-menu .Dropdown-option");
    private static final By COLOR_BLACK = By.id("black");
    private static final By COLOR_GREY = By.id("grey");
    private static final By COMMENT = By.xpath("//input[@placeholder='Комментарий для курьера']");
    private static final By ORDER = By.xpath("//button[contains(@class,'Button_Middle__') and text()='Заказать']");
    private static final By CONFIRM_YES = By.xpath("//button[normalize-space()='Да']");
    private static final By MODAL_TITLE = By.cssSelector(".Order_ModalHeader__3FDaJ");
    private static final By MODAL_TEXT = By.cssSelector(".Order_Modal__YZ-d3");

    public OrderPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    public void fillFirstForm(String name, String surname, String address, String metro, String phone) {
        type(NAME, name);
        type(SURNAME, surname);
        type(ADDRESS, address);

        click(METRO);
        type(METRO, metro);
        // выбрать точное совпадение станции
        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(METRO_OPTIONS));
        options.stream()
                .filter(o -> o.getText().trim().equalsIgnoreCase(metro))
                .findFirst()
                .orElse(options.get(0))
                .click();

        type(PHONE, phone);
        click(NEXT);
        wait.until(ExpectedConditions.visibilityOfElementLocated(DATE));
    }

    public void fillSecondForm(String dateStr, String period, String color, String comment) {
        // дата: если пусто — +3 дня от сегодня
        String dateToUse = (dateStr == null || dateStr.isBlank())
                ? LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : dateStr;
        type(DATE, dateToUse);
        driver.findElement(DATE).sendKeys(Keys.ENTER);

        click(RENTAL_DROPDOWN);
        List<WebElement> periods = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(RENTAL_OPTIONS));
        periods.stream()
                .filter(p -> p.getText().toLowerCase().contains(period.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Период аренды не найден: " + period))
                .click();

        if (color != null) {
            switch (color.toLowerCase()) {
                case "black": click(COLOR_BLACK); break;
                case "grey":  click(COLOR_GREY);  break;
            }
        }

        if (comment != null) type(COMMENT, comment);
    }

    public void makeOrder() {
        click(ORDER);
        click(CONFIRM_YES);
        wait.until(ExpectedConditions.visibilityOfElementLocated(MODAL_TITLE));
    }

    public boolean isOrderSuccess() {
        try {
            WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(MODAL_TITLE));
            WebElement text = wait.until(ExpectedConditions.visibilityOfElementLocated(MODAL_TEXT));
            String t1 = title.getText();
            String t2 = text.getText();
            return t1.contains("Заказ оформлен") || t2.matches("(?s).*Заказ оформлен.*№\\s*\\d+.*");
        } catch (TimeoutException e) {
            return false;
        }
    }

    private void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private void type(By locator, String value) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        el.clear();
        el.sendKeys(value);
    }
}