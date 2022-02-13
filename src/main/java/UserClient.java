import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends UserSpecification {

    private static final String USER_PATH = "api/auth/";

    @Step ("Создать пользователя")
    public ValidatableResponse create(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(USER_PATH + "register")
                .then();
    }

    @Step ("Логин пользователя в системе")
    public ValidatableResponse login(UserCredentials credentials) {
        return given()
                .spec(getBaseSpec())
                .body(credentials)
                .when()
                .post(USER_PATH + "login")
                .then();
    }

    @Step ("Изменить данные пользователя")
    public ValidatableResponse changeData(User user, String bearerToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(bearerToken)
                .body(user)
                .when()
                .patch(USER_PATH + "user")
                .then();
    }
    @Step ("Удаление пользователя")
    public static ValidatableResponse delete(String bearerToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(bearerToken)
                .when()
                .delete(USER_PATH + "user")
                .then();
    }
}
