import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {
    int days = 7;

    @BeforeAll
    public static void SetUpCLass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        open("http://localhost:9999/");
    }

    String getFormattedDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    int yearOffset(int days) {
        return LocalDate.now().plusDays(days).getYear() - LocalDate.now().plusDays(3).getYear();
    }

    int monthOffset(int days) {
        return LocalDate.now().plusDays(days).getMonthValue() - LocalDate.now().plusDays(3).getMonthValue();
    }

    @Test
    void shouldSendForm() {
        $("[data-test-id=city] .input__control").setValue("Нижний Новгород");
        $("[data-test-id=date] [placeholder=\"Дата встречи\"]").sendKeys(Keys.chord(Keys.CONTROL + "A"), Keys.BACK_SPACE, LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.uuuu")));
        $("[data-test-id=name] [name=name]").setValue("Стрельников Сергей");
        $("[data-test-id=phone] [name=phone]").setValue("+79991236558");
        $("[data-test-id=agreement]>.checkbox__box").click();
        $("button>.button__content").click();
        $("[data-test-id=notification]").shouldBe(visible, Duration.ofSeconds(15)).shouldHave(exactText("Успешно! Встреча успешно забронирована на " + LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.uuuu"))));
        $(".icon_name_close").click();
    }

    @Test
    void shouldSendFormAnotherVar() {
        $("[data-test-id=city] .input__control").setValue("Ни");
        $$(".menu-item__control").findBy(text("Нижний Новгород")).click();
        $(".icon-button__text>.icon_name_calendar").click();
        if (yearOffset(days) > 0) {
            for (int i = 0; i < yearOffset(days); i++) {
                $(".calendar__arrow_direction_right[data-step='12']").click();
            }
            if (monthOffset(days) > 0) {
                for (int i = 0; i < monthOffset(days); i++) {
                    $(".calendar__arrow_direction_right[data-step='1']").click();
                }
            } else {
                for (int i = 0; i > monthOffset(days); i--) {
                    $(".calendar__arrow_direction_left[data-step='-1']").click();
                }
            }
        } else {
            for (int i = 0; i < monthOffset(days); i++) {
                $(".calendar__arrow_direction_right[data-step='1']").click();
            }
        }
        $$(".calendar__day").findBy(text(LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("d")))).click();
        $("[data-test-id=name] [name=name]").setValue("Стрельников Сергей");
        $("[data-test-id=phone] [name=phone]").setValue("+79991236558");
        $("[data-test-id=agreement]>.checkbox__box").click();
        $("button>.button__content").click();
        $("[data-test-id=notification]").shouldBe(visible, Duration.ofSeconds(15)).shouldHave(exactText("Успешно! Встреча успешно забронирована на " + getFormattedDate(days)));
    }
}