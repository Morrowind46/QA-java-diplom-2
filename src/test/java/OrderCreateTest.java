import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class OrderCreateTest {

    private User user;
    private UserClient userClient;
    private Ingredients ingredients;
    public OrderClient orderClient;
    String bearerToken;

    // Создание рандомного пользователя и бургера
    @Before
    public void setUp() {
        user = User.getRandom();
        userClient = new UserClient();
        ingredients = Ingredients.getRandomBurger();
        orderClient = new OrderClient();
    }

    @Test
    @Description ("Создание заказа. Зарегистрированный пользователь")
    public void orderCanBeCreatedAuthUser (){

        // Создание пользователя
        ValidatableResponse userResponse = userClient.create(user);
        // Полученеи токена
        bearerToken = userResponse.extract().path("accessToken");
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
        assertTrue("The order has not been created", orderCreated);
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
        assertTrue("The order has not been created", orderCreated);
        // Проверка что присвоен номер заказа
        assertThat("The order number is missing", orderNumber, is(not(0)));
    }

    @Test
    @Description ("Создание заказа без ингредиентов")
    public void orderCantBeCreatedWithOutIngredients (){

        // Создание пользователя
        ValidatableResponse userResponse = userClient.create(user);
        // Полученеи токена
        bearerToken = userResponse.extract().path("accessToken");
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
        assertFalse("The order has been created", orderNotCreated);
        // Проверка что присвоен номер заказа
        assertEquals("The error message is not correct", "Ingredient ids must be provided", errorMessage);
    }

    @Test //(expected = ClassCastException.class)
    @Description ("Создание заказа с невалидными ингридиентами")
    public void orderCantBeCreatedWithIncorrectIngredients (){

        // Создание пользователя
        ValidatableResponse userResponse = userClient.create(user);
        // Полученеи токена
        bearerToken = userResponse.extract().path("accessToken");
        // Создание заказа
        ValidatableResponse orderResponse = orderClient.create(bearerToken,Ingredients.getIncorrectIngredients());
        // Получение статус кода с тела создания заказа
        int statusCode = orderResponse.extract().statusCode();

        // Проверка что статус код соответсвует ожиданиям
        assertThat ("Status code is not correct", statusCode, equalTo(500));
    }
}