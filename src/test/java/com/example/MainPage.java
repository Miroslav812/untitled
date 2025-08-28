package com.example;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class MainPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By cookieButton = By.id("rcc-confirm-button");
    private final By orderButtons = By.xpath("//button[contains(text(), 'Заказать')]");
    private final By faqSection = By.className("Home_FAQ__3uVm4");
    private final By faqQuestions = By.xpath("//div[contains(@class,'accordion__button')]");
    private final By faqAnswers = By.xpath("//div[contains(@class,'accordion__panel')]");

    public MainPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open() {
        driver.get("https://qa-scooter.praktikum-services.ru/");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        acceptCookies();
    }

    private void acceptCookies() {
        try {
            WebElement cookie = wait.withTimeout(Duration.ofSeconds(3))
                    .until(ExpectedConditions.elementToBeClickable(cookieButton));
            cookie.click();
        } catch (Exception ignored) {
        } finally {
            wait.withTimeout(Duration.ofSeconds(10));
        }
    }

    public OrderPage clickOrderButton(boolean topButton) {
        List<WebElement> buttons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(orderButtons));
        if (buttons.size() < 2) {
            throw new RuntimeException("Не найдены обе кнопки 'Заказать'");
        }
        WebElement button = topButton ? buttons.get(0) : buttons.get(1);
        scrollAndClick(button);
        return new OrderPage(driver);
    }

    public void scrollToFaq() {
        WebElement faq = wait.until(ExpectedConditions.presenceOfElementLocated(faqSection));
        scrollIntoView(faq);
    }

    public void clickFaqQuestion(int index) {
        List<WebElement> questions = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(faqQuestions));
        if (index < questions.size()) {
            scrollAndClick(questions.get(index));
        } else {
            throw new IllegalArgumentException("FAQ question " + index + " not found");
        }
    }

    public String getFaqAnswerText(int index) {
        List<WebElement> answers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(faqAnswers));
        if (index < answers.size()) {
            return answers.get(index).getText().trim();
        }
        throw new IllegalArgumentException("FAQ answer " + index + " not found");
    }

    public boolean isFaqAnswerDisplayed(int index) {
        List<WebElement> answers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(faqAnswers));
        return index < answers.size() && answers.get(index).isDisplayed();
    }

    public WebElement getFaqAnswerElement(int index) {
        List<WebElement> answers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(faqAnswers));
        if (index < answers.size()) {
            return answers.get(index);
        }
        throw new IllegalArgumentException("FAQ answer " + index + " not found");
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    private void scrollAndClick(WebElement element) {
        scrollIntoView(element);
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }
}
