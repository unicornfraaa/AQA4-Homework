package ru.netology;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class СardDeliveryServiceTest {
    public String generateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

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
    void shouldBeValid() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='notification'] .notification__content")
                .shouldBe(visible, Duration.ofSeconds(15))
                .should(exactText("Встреча успешно забронирована на " + meetingDate));
    }

    @Test
    void shouldAcceptACityWithADash() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("Йошкар-Ола");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='notification'] .notification__content")
                .shouldBe(visible, Duration.ofSeconds(15))
                .should(exactText("Встреча успешно забронирована на " + meetingDate));
    }

    @Test
    void shouldNotAcceptANonAdministrativeCity() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("Волжск");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='city'].input_invalid").shouldBe(visible, Duration.ofSeconds(5))
                .should(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldNotAcceptEnglishLetters() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("Kazan");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='city'].input_invalid").shouldBe(visible, Duration.ofSeconds(5))
                .should(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldNotAcceptAnEmptyCity() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='city'].input_invalid").shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text("Поле обязательно для заполнения"));
    }

    @Test
    void shouldNotAcceptLastDate() {
        String meetingDate = generateDate(1);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $(withText("невозможен")).should(visible, Duration.ofSeconds(5));
        $x("//*[@data-test-id=\"notification\"]").shouldNot(visible, Duration.ofSeconds(10));
    }

    @Test
    void shouldNotAcceptMeetingToday() {
        String meetingDate = generateDate(0);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $(withText("невозможен")).should(visible, Duration.ofSeconds(5));
        $x("//*[@data-test-id=\"notification\"]").shouldNot(visible, Duration.ofSeconds(10));
    }

    @Test
    void shouldNotAcceptForTomorrowDay() {
        String meetingDate = generateDate(1);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $(withText("невозможен")).should(visible, Duration.ofSeconds(5));
        $x("//*[@data-test-id=\"notification\"]").shouldNot(visible, Duration.ofSeconds(10));
    }

    @Test
    void shouldNotAcceptForDayAfterTomorrow() {
        String meetingDate = generateDate(2);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $(withText("невозможен")).should(visible, Duration.ofSeconds(5));
        $x("//*[@data-test-id=\"notification\"]").shouldNot(visible, Duration.ofSeconds(10));
    }

    @Test
    void shouldAcceptForDayAfterTwoDays() {
        String meetingDate = generateDate(3);
        $("[data-test-id='city'] input").setValue("Йошкар-Ола");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='notification'] .notification__content")
                .shouldBe(visible, Duration.ofSeconds(15))
                .should(exactText("Встреча успешно забронирована на " + meetingDate));
    }

    @Test
    void shouldAcceptDoubleSurname() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов-Петров Иван");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='notification'] .notification__content")
                .shouldBe(visible, Duration.ofSeconds(15))
                .should(exactText("Встреча успешно забронирована на " + meetingDate));
    }

    @Test
    void shouldAcceptDoubleName() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван-Петр");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='notification'] .notification__content")
                .shouldBe(visible, Duration.ofSeconds(15))
                .should(exactText("Встреча успешно забронирована на " + meetingDate));
    }

    @Test
    void shouldNotAcceptEngName() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Ivanov Ivan");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='name'].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldNotAcceptSymbolsInName() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("*Иванов Иван*");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='name'].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldNotAcceptNumbersInName() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов 2Иван");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='name'].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldNotAcceptEmptyName() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='name'].input_invalid").shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text("Поле обязательно для заполнения"));
    }

    @Test
    void shouldNotAcceptEmptyTel() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='phone'].input_invalid").shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text("Поле обязательно для заполнения"));
    }

    @Test
    void shouldNotAcceptLettersInTel() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("qwerty");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='phone'].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldNotAcceptAnySymbolsInTel() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("*");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='phone'].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldNotAcceptLessThan11NumbersInTel() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+1234567890");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='phone'].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldNotAcceptTelStartsFrom8() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("88005553535");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='phone'].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldNotAcceptTelWithPlusAtTheEnd() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("79999999999+");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='phone'].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldNotAcceptWithoutCheckBox() {
        String meetingDate = generateDate(4);
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='agreement'].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text("Я соглашаюсь с условиями обработки и использования моих персональных данных"));
    }
}