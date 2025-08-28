package com.example;

import com.example.MainPage;
import com.example.OrderPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderTest {
    private WebDriver driver;

    @BeforeEach
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    private static Stream<Object[]> orderDataProvider() {
        return Stream.of(
                new Object[]{"Иван", "Петров", "ул. Ленина, 11", "Сокольники", "+79123456000",
                        "01.01.2025", "сутки", "black", "Комментарий 11", true},
                new Object[]{"Мария", "Сидорова", "пр. Мира, 251", "Черкизовская", "+79234567000",
                        "15.01.2025", "двое суток", "grey", "Комментарий 21", false}
        );
    }

    @ParameterizedTest
    @MethodSource("orderDataProvider")
    public void testOrderCreation(
            String name, String surname, String address, String metro, String phone,
            String date, String period, String color, String comment, boolean useTopButton
    ) {
        MainPage mainPage = new MainPage(driver);
        mainPage.open();

        OrderPage orderPage = mainPage.clickOrderButton(useTopButton);

        orderPage.fillFirstForm(name, surname, address, metro, phone);
        orderPage.fillSecondForm(date, period, color, comment);
        orderPage.makeOrder();

        assertTrue(orderPage.isOrderSuccess(), "Заказ не был успешно создан!");
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
