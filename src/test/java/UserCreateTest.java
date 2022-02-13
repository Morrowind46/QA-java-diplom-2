import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

public class UserCreateTest {

    private User user;
    private UserClient userClient;
    private String accessToken;
    private String bearerToken;

    @Before
    public void setUp() {
        user = User.getRandom();
        userClient = new UserClient();
    }

    @After //Удаляем созданого пользователя
    public void tearDown() {
        UserClient.delete(bearerToken);
    }

    @Test
    @DisplayName("Пользователя можно создать")
    @Description("успешный запрос возвращает success: true")
    public void checkUserCanBeCreated() {
        // Arrange
        ValidatableResponse response = userClient.create(user);
        // Act
        int statusCode = response.extract().statusCode();
        boolean isUserCreated = response.extract().path("success");
        accessToken = userClient.login(UserCredentials.from(user)).extract().path("accessToken");
        bearerToken = accessToken.substring(7);
        // Assert
        assertThat("User is not created", isUserCreated, is(true));
        assertThat("Status code is incorrect", statusCode, equalTo(200));
        assertThat("User access token is incorrect", accessToken, is(not("")));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых пользователей")
    @Description("если создать пользователя с логином, который уже есть - возвращается ошибка")
    public void duplicateUserCannotBeCreated() {
        userClient.create(user);
        ValidatableResponse response = userClient.create(user);
        accessToken = userClient.login(UserCredentials.from(user)).extract().path("accessToken");
        bearerToken = accessToken.substring(7);
        // Цель: проверить, что нельзя создать воторго пользователя с такими же данными
        // 1. Создать пользователя первый раз
        // 2. Создать пользователя во второй раз с теми же данными
        // 3. Второй запрос возвращает ошибку

        int statusCode = response.extract().statusCode();
        String errorMessage = response.extract().path("message");

        // Assert
        assertThat("Status code is incorrect", statusCode, equalTo(403));
        assertThat("Duplicate user has been created", errorMessage, equalTo("User already exists"));
    }

}
