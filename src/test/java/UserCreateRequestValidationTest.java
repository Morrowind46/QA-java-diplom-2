import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class UserCreateRequestValidationTest {

    private final User user;
    private final int expectedStatus;
    private final String expectedErrorMessage;

    public UserCreateRequestValidationTest(User user, int expectedStatus, String expectedErrorMessage) {
        this.user = user;
        this.expectedStatus = expectedStatus;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][] {
                {User.getUserWithoutName(), 403, "Email, password and name are required fields"},
                {User.getUserWithoutPassword(), 403, "Email, password and name are required fields"},
                {User.getUserWithoutEmail(), 403, "Email, password and name are required fields"}
        };
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить одно из обязательных полей")
    @Description("Если нет одного из полей, вернётся код ответа 403 Forbidden")
    public void invalidRequestIsNotAllowed() {

        ValidatableResponse response = new UserClient().create(user);
        // Цель: проверить, что нельзя создать пользователя с невалидным реквестом
        // Создаем пользователя только с эмейлом, делаем запрос, проверяем, что создание не состоялось

        String actualMessage = response.extract().path("message");
        int code = response.extract().statusCode();

        // asserts
        assertEquals (expectedErrorMessage, actualMessage);
        assertEquals (expectedStatus, code);
    }
}
