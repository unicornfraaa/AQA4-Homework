package ru.netology;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.withText;
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
    void shouldBeValid() {
//        $x("//*[@data-test-id = 'city']").click();
//        $x("//*[@data-test-id = 'city']").setValue("Казань");
//        $("[placeholder=Город]").val("Казань");
//        $("[placeholder=Город]").sendKeys("Казань");
//        $("[placeholder=Город]").doubleClick().sendKeys("Казань");
//        $("[placeholder=Город]").setValue("Казань");
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Йошкар-Ола");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Волжск");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Kazan");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='city'].input_invalid").shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text("Поле обязательно для заполнения"));
    }

//    @Test
//    void test() {
//        $("[data-test-id='city'] input").setValue("Казань");
//        $("[data-test-id='date'] input").doubleClick().sendKeys("10.10.2020");
//        $("[data-test-id='name'] input").setValue("Иванов Иван");
//        $("[data-test-id='phone'] input").setValue("+79999999999");
//        $("[data-test-id='agreement'] span").click();
//        $x("//*[contains(text(), 'Забронировать')]").click();
////        $("[data-test-id='date'].notification__content")
//        $("[data-test-id='date'].input_invalid")
//                .shouldNot(visible, Duration.ofSeconds(10))
//                .shouldHave(text("Заказ на выбранную дату невозможен"));
//    }

    @Test
    void shouldNotAcceptLastDate() {
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Йошкар-Ола");
        String meetingDate = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван-Петр");
        $("[data-test-id='phone'] input").setValue("+79999999999");
        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='notification'] .notification__content")
                .shouldBe(visible, Duration.ofSeconds(15))
                .should(exactText("Встреча успешно забронирована на " + meetingDate));
    }

    // тест на имя без фамилии должен выводить ошибку, а выводит успех
//    @Test
//    void shouldNotAcceptWithoutSurname() {
//        $("[data-test-id='city'] input").setValue("Казань");
//    String meetingDate = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
//        $("[data-test-id=date] input").doubleClick.sendKeys(meetingDate);
//        $("[data-test-id='name'] input").setValue("Иван");
//        $("[data-test-id='phone'] input").setValue("+79999999999");
//        $("[data-test-id='agreement'] span").click();
//        $x("//*[contains(text(), 'Забронировать')]").click();
//        $("[data-test-id='notification'] .notification__content")
//                .shouldBe(visible, Duration.ofSeconds(15))
//                .should(exactText("Встреча успешно забронирована на " + meetingDate));

    //$("[data-test-id='name'].input_invalid").shouldBe(visible, Duration.ofSeconds(5))
    //                .shouldHave(text("Поле обязательно для заполнения"));
//    }

    @Test
    void shouldNotAcceptEngName() {
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
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
        $("[data-test-id='city'] input").setValue("Казань");
        String meetingDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+79999999999");
//        $("[data-test-id='agreement'] span").click();
        $x("//*[contains(text(), 'Забронировать')]").click();
        $("[data-test-id='agreement'].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text("Я соглашаюсь с условиями обработки и использования моих персональных данных"));
    }
}