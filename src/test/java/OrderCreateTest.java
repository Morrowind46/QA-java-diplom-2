import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

public class OrderCreateTest {

    private User user;
    private UserClient userClient;
    private Ingredients ingredients;
    public OrderClient orderClient;
    private String accessToken;
    private String bearerToken;

    // Создание рандомного пользователя и бургера
    @Before
    public void setUp() {
        user = User.getRandom();
        userClient = new UserClient();
        ingredients = Ingredients.getRandomBurger();
        orderClient = new OrderClient();
    }

    @After //Удаляем созданого пользователя
    public void tearDown() {
        UserClient.delete(bearerToken);
    }

    @Test
    @Description ("Создание заказа. Зарегистрированный пользователь")
    public void orderCanBeCreatedAuthUser (){

        // Создание пользователя
        ValidatableResponse userResponse = userClient.create(user);
        // Полученеи токена
        accessToken = userResponse.extract().path("accessToken");
        bearerToken = accessToken.substring(7);
        // Создание заказа
        ValidatableResponse orderResponse = orderClient.create(bearerToken, ingredients);
        // Получение статус кода с тела создания заказа
        int statusCode = orderResponse.extract().statusCode();
        // Получение ответа
        boolean orderCreated = orderResponse.extract().path("success");
        // Получение номера созданого заказа
        int orderNumber = orderResponse.extract().path("order.number");

        // Проверка что статус код соответствует ожиданиям
        assertThat ("Status code is not correct", statusCode, equalTo(200));
        // Проверка что заказ создался
        assertThat("The order has not been created", orderCreated, is(true));
        // Проверка что присвоен номер заказа
        assertThat("The order number is missing", orderNumber, is(not(0)));
    }

    @Test
    @Description ("Создание заказа. Не зарегистрированный пользователь")
    public void orderCanBeCreatedNonAuthUser (){

        // Пустой токен
        bearerToken = "";
        // Создание заказа
        ValidatableResponse orderResponse = orderClient.create(bearerToken,ingredients);
        // Получение статус кода с тела создания заказа
        int statusCode = orderResponse.extract().statusCode();
        // Получение ответа
        boolean orderCreated = orderResponse.extract().path("success");
        // Получение номера созданого заказа
        int orderNumber = orderResponse.extract().path("order.number");

        // Проверка что статус код соответствует ожиданиям
        assertThat ("Status code is not correct", statusCode, equalTo(200));
        // Проверка что заказ создался
        assertThat("The order has not been created", orderCreated, is(true));
        // Проверка что присвоен номер заказа
        assertThat("The order number is missing", orderNumber, is(not(0)));
    }

    @Test
    @Description ("Создание заказа без ингредиентов")
    public void orderCantBeCreatedWithOutIngredients (){

        // Создание пользователя
        ValidatableResponse userResponse = userClient.create(user);
        // Полученеи токена
        accessToken = userResponse.extract().path("accessToken");
        bearerToken = accessToken.substring(7);
        // Создание заказа без ингредиентов
        ValidatableResponse orderResponse = orderClient.create(bearerToken, Ingredients.getNullIngredients());
        // Получение статус кода с тела создания заказа
        int statusCode = orderResponse.extract().statusCode();
        // Получение ответа
        boolean orderNotCreated = orderResponse.extract().path("success");
        // Получение сообщения об ошибке
        String errorMessage = orderResponse.extract().path("message");

        // Проверка что статус код соответсвует ожиданиям
        assertThat ("Status code is not correct", statusCode, equalTo(400));
        // Проверка что заказ создался
        assertThat("The order has not been created", orderNotCreated, is(false));
        // Проверка что присвоен номер заказа
        assertEquals("The error message is not correct", "Ingredient ids must be provided", errorMessage);
    }

    @Test //(expected = ClassCastException.class)
    @Description ("Создание заказа с невалидными ингридиентами")
    public void orderCantBeCreatedWithIncorrectIngredients (){

        // Создание пользователя
        ValidatableResponse userResponse = userClient.create(user);
        // Полученеи токена
        accessToken = userResponse.extract().path("accessToken");
        bearerToken = accessToken.substring(7);
        // Создание заказа
        ValidatableResponse orderResponse = orderClient.create(bearerToken,Ingredients.getIncorrectIngredients());
        // Получение статус кода с тела создания заказа
        int statusCode = orderResponse.extract().statusCode();

        // Проверка что статус код соответсвует ожиданиям
        assertThat ("Status code is not correct", statusCode, equalTo(500));
    }
}