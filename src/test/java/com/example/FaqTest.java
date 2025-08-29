package com.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FaqTest {

    private WebDriver driver;
    private String browser;

    @BeforeEach
    void setUp() {
        // Получаем браузер из параметра системы: mvn test -Dbrowser=firefox
        browser = System.getProperty("browser", "firefox").toLowerCase();

        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            default:
                throw new IllegalArgumentException("Браузер не поддерживается: " + browser);
        }

        driver.manage().window().maximize();
        System.out.println("=== Запуск тестов FAQ в браузере: " + browser + " ===");
    }

    @ParameterizedTest(name = "FAQ #{0} — проверка текста ответа")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
    void testFaqAccordion(int index) {
        MainPage main = new MainPage(driver);
        main.open();
        main.scrollToFaq();
        clickFaqQuestion(index);

        String actual = normalize(main.getFaqAnswerText(index));

        // Ключевые слова для каждого FAQ (каждому FAQ — свои ключевые слова)
        String[][] expectedKeywords = {
                {"сутки", "400 рублей", "оплата", "курьеру", "наличными", "картой"},
                {"один заказ", "один самокат", "несколько заказов", "один за другим"},
                {"оформляете заказ", "8 мая", "привозим", "отсчет", "оплат"},
                {"только начиная", "завтрашнего дня", "скоро", "расторопнее"},
                {"пока что нет", "позвонить", "поддержку", "1010"},
                {"самокат", "зарядк", "восемь суток", "без передышек"},
                {"да", "пока самокат", "не привезли", "штрафа", "объяснительной записки", "не попросим"},
                {"да", "обязательно", "всем самокатов", "Москве", "Московской области"}
        };

        assertContainsKeywords(actual, expectedKeywords[index]);
    }

    private void assertContainsKeywords(String actual, String... keywords) {
        for (String kw : keywords) {
            String normalizedKw = normalize(kw);
            assertTrue(actual.contains(normalizedKw),
                    () -> "Текст ответа некорректен. Ожидали ключевое слово: \"" + kw + "\". Факт: \"" + actual + "\"");
        }
    }

    private void clickFaqQuestion(int index) {
        WebElement question = driver.findElement(By.id("accordion__heading-" + index));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", question);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", question);
    }

    private String normalize(String s) {
        if (s == null) return "";
        return s.toLowerCase()
                .replace('ё', 'е')
                .replaceAll("[^a-zа-я0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}