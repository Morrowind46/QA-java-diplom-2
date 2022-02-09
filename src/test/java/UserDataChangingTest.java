import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserDataChangingTest {

    private User user;
    private User updatedUser;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        user = User.getRandom();
        updatedUser = User.getRandom();
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    @Description("успешный запрос возвращает success: true")
    public void checkChangingUserData() {

        //создать пользователя
        //получить токен
        //авторизоваться с помощью токена (передать параметор с токеном в метод),
        //изменить данные о пользователе
        //проверить изменения

        // Arrange
        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken");
        String bearerToken = accessToken.substring(7);
        //System.out.println(accessToken);
        //System.out.println(bearerToken);
        // Act
        ValidatableResponse response2 = userClient.changeData(updatedUser, bearerToken);
        boolean isUserDataChanged = response2.extract().path("success");
        int statusCode = response2.extract().statusCode();
        // Assert
        // System.out.println(isUserDataChanged);
        assertTrue("User data is not changed", isUserDataChanged);
        assertThat("Status code is incorrect", statusCode, equalTo(200));
        assertThat("User access token is incorrect", accessToken, is(not("")));
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    @Description("успешный запрос возвращает success: false")
    public void checkChangingUserDataWithoutAuthorization() {
        //создать пользователя
        //изменить данные о пользователе
        //проверить изменения

        // Arrange
        userClient.create(user);
        // Act
        ValidatableResponse response = userClient.changeData(updatedUser, "");
        boolean isUserDataChanged = response.extract().path("success");
        int statusCode = response.extract().statusCode();
        // Assert
        // System.out.println(isUserDataChanged);
        assertFalse("User data is not changed", isUserDataChanged);
        assertThat("Status code is incorrect", statusCode, equalTo(401));
    }
}
