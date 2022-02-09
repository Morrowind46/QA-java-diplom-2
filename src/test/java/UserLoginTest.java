import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserLoginTest {

    private User user;
    private UserClient userClient;
    private String accessToken;


    @Before
    public void setUp() {
        userClient = new UserClient();
        user = User.getRandom();
    }

//    @After
//    public void  tearDown() {
//    }

    @Test
    @DisplayName("логин под существующим пользователем")
    @Description("успешный запрос возвращает success: true")
    public void courierCanLogIn() {

        //Act
        userClient.create(user);
        ValidatableResponse response = userClient.login(UserCredentials.from(user));
        //без паттерна "фабичный метод":  ValidatableResponse response = userClient.login(new UserCredentials(user.email, user.password));
        accessToken = response.extract().path("accessToken");
        int statusCodeSuccessfulLogin = response.extract().statusCode();
        //Assert
        assertThat("User access token is incorrect", accessToken, is(not("")));
        assertThat(statusCodeSuccessfulLogin, equalTo(200));
    }
}