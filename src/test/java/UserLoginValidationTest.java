import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class UserLoginValidationTest {

    private UserClient userClient =  new UserClient();
    private static User user = User.getRandom();

    private final UserCredentials userCredentials;
    private final int expectedStatus;
    private final String expectedErrorMessage;
    private String accessToken;
    private String bearerToken;

    public UserLoginValidationTest(UserCredentials userCredentials, int expectedStatus, String expectedErrorMessage) {
        this.userCredentials = userCredentials;
        this.expectedStatus = expectedStatus;
        this.expectedErrorMessage = expectedErrorMessage;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][]{
                {UserCredentials.getCredentialsWithPasswordOnly (user), 401, "email or password are incorrect"},
                {UserCredentials.getCredentialsWithEmailOnly (user), 401, "email or password are incorrect"},
                {UserCredentials.getCredentialsWithRandomEmail (user), 401 , "email or password are incorrect"},
                {UserCredentials.getCredentialsWithRandomPassword (user), 401 , "email or password are incorrect"},
        };
    }

    @After //Удаляем созданого пользователя
    public void tearDown() {
        UserClient.delete(bearerToken);
    }

    @Test
    @DisplayName("Для авторизации нужно передать все обязательные поля")
    @Description("1. если нет поля Email, запрос возвращает ошибку" +
            "2. если нет поля Password, запрос возвращает ошибку" +
            "3. система вернёт ошибку, если неправильно указать Email" +
            "4. система вернёт ошибку, если неправильно указать Password" )
    public void UserLoginValidationTest() {

        //Arrange
        ValidatableResponse response = userClient.create(user);
        ValidatableResponse login = new UserClient().login(userCredentials);
        accessToken = response.extract().path("accessToken");
        bearerToken = accessToken.substring(7);
        //Act
        int ActualStatusCode = login.extract ().statusCode();
        String actualMessage = login.extract ().path ("message");
        //Assert
        assertEquals ("Status code is incorrect",expectedStatus, ActualStatusCode);
        assertEquals ("User access token is incorrect", expectedErrorMessage, actualMessage);
    }
}
