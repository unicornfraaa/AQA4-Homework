package ru.netology;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.*;

public class cardDeliveryServiceTest {

    @BeforeEach
    void setUp() {
        open("http://localhost:9999/");
        Configuration.holdBrowserOpen = true;
    }

    @AfterEach
    void memoryClear() {
        clearBrowserCookies();
        clearBrowserLocalStorage();
    }

    @Test
    void shouldBeValid () {
//        $x("//*[@data-test-id = 'city']").click();
//        $x("//*[@data-test-id = 'city']").setValue("Казань");

//        $("[placeholder=Город]").setValue("Казань");

//        $("[placeholder=Город]").val("Казань");

//        $("[placeholder=Город]").sendKeys("Казань");
        $("[placeholder=Город]").doubleClick().sendKeys("Казань");

    }
}