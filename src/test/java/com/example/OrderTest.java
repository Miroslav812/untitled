package com.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderTest {

    private WebDriver driver;
    private String browser = "firefox"; // Можно менять на "chrome" или "firefox"

    @BeforeEach
    void setUp() {
        switch (browser.toLowerCase()) {
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
    }

    static Stream<Object[]> orderData() {
        return Stream.of(
                new Object[]{"Иван", "Петров", "ул. Ленина, 11", "Сокольники", "79995554433",
                        "", "сутки", "black", "Позвонить за 10 минут", true},
                new Object[]{"Мария", "Сидорова", "пр. Мира, 251", "Черкизовская", "79261234567",
                        "", "двое суток", "grey", "Оставить у охраны", false}
        );
    }

    @ParameterizedTest(name = "Оформление заказа (useTopButton={9})")
    @MethodSource("orderData")
    void testOrderCreation(String name, String surname, String address, String metro, String phone,
                           String date, String period, String color, String comment, boolean useTopButton) {

        System.out.println("=== START ORDER TEST ===");
        System.out.printf("Браузер: %s%n", browser);
        System.out.printf("Данные заказа: name=%s, surname=%s, address=%s, metro=%s, phone=%s, date=%s, period=%s, color=%s, comment=%s, useTopButton=%s%n",
                name, surname, address, metro, phone, date, period, color, comment, useTopButton);

        MainPage main = new MainPage(driver);
        main.open();

        OrderPage order = main.clickOrderButton(useTopButton);
        order.fillFirstForm(name, surname, address, metro, phone);
        order.fillSecondForm(date, period, color, comment);
        order.makeOrder();

        boolean success = order.isOrderSuccess();

        if (!success) {
            System.err.println("⚠️ Заказ не оформился! Возможно баг в приложении.");
        }

        assertTrue(success, "Заказ не оформился/не видно номера заказа.");
        System.out.println("=== ORDER TEST PASSED ===\n");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }
}