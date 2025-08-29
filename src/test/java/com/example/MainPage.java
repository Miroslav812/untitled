package com.example;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class MainPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final String URL = "https://qa-scooter.praktikum-services.ru/";

    private final By cookieButton = By.id("rcc-confirm-button");
    private final By orderButtons = By.xpath("//button[contains(text(),'Заказать')]");
    private final By faqSection = By.cssSelector(".Home_FAQ__3uVm4");
    private final By faqQuestions = By.cssSelector(".accordion__button");
    private final By faqAnswers = By.cssSelector(".accordion__panel");

    public MainPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open() {
        driver.get(URL);
        acceptCookiesIfAny();
    }

    private void acceptCookiesIfAny() {
        try {
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(cookieButton));
            btn.click();
        } catch (TimeoutException ignored) {
        }
    }

    public void scrollToFaq() {
        WebElement faq = wait.until(ExpectedConditions.visibilityOfElementLocated(faqSection));
        scrollIntoView(faq);
    }

    public void clickFaqQuestion(int index) {
        List<WebElement> questions = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(faqQuestions, index));
        WebElement question = questions.get(index);
        scrollIntoView(question);
        // Безопасный клик через JS
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", question);
        // Немного подождем, чтобы ответ успел появиться
        wait.until(ExpectedConditions.visibilityOf(getFaqAnswerElement(index)));
    }

    public String getFaqAnswerText(int index) {
        WebElement answer = getFaqAnswerElement(index);
        wait.until(ExpectedConditions.visibilityOf(answer));
        return normalize(answer.getText());
    }

    private WebElement getFaqAnswerElement(int index) {
        List<WebElement> answers = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(faqAnswers, index));
        return answers.get(index);
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'center'});", element);
    }

    // Метод нормализации текста для корректного сравнения
    private String normalize(String s) {
        if (s == null) return "";
        return s.toLowerCase()
                .replace('ё', 'е')
                .replace('—', '-')
                .replaceAll("[^a-zа-я0-9\\s-]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public OrderPage clickOrderButton(boolean useTopButton) {
        List<WebElement> buttons = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(orderButtons, 0));
        WebElement target = buttons.size() == 1 ? buttons.get(0) : (useTopButton ? buttons.get(0) : buttons.get(1));
        scrollIntoView(target);
        wait.until(ExpectedConditions.elementToBeClickable(target)).click();
        return new OrderPage(driver);
    }
}