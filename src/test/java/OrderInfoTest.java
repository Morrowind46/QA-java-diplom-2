import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.Map;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class OrderInfoTest {

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
    @Description("Получение списка заказов")
    public void orderInfoCanBeGet (){
        bearerToken = "";
        // Запрос информации о заказах
        ValidatableResponse orderInfo = orderClient.orderInfo();
        // Получение статус кода запроса информации о заказах
        int statusCode = orderInfo.extract().statusCode();
        // Получение ответа
        boolean orderInfoGet = orderInfo.extract().path("success");
        // Получение тела списка заказов
        List<Map<String, Object>> ordersList = orderInfo.extract().path("orders");

        // Проверка что статус код соответсвует ожиданиям
        assertThat ("Status code is not correct", statusCode, equalTo(200));
        // Проверка что информация о заказах получена
        assertThat("Information about orders has not been received", orderInfoGet, is(true));
        // Проверка что список заказов не пустой
        assertThat("Orders list empty", ordersList, is(not(0)));
    }

    @Test
    @Description("Получение списка заказов авторизованного пользователя")
    public void orderUserInfoCanBeGetAuthUser (){

        // Создание пользователя
        userClient.create(user);
        // Авторизация созданого пользователя
        ValidatableResponse login = userClient.login(UserCredentials.from(user));
        // Получение токена пользователя
        accessToken = login.extract().path("accessToken");
        bearerToken = accessToken.substring(7);
        // Информация о заказах пользователя
        ValidatableResponse orderInfo = orderClient.userOrderInfo(bearerToken);
        // Получение статус кода запроса информации о заказах клиента
        int statusCode = orderInfo.extract().statusCode();
        // Получение ответа
        boolean orderCreated = orderInfo.extract().path("success");
        // Получение тела списка заказов
        List<Map<String, Object>> ordersList = orderInfo.extract().path("orders");

        // Проверка что статус код соответствует ожиданиям
        assertThat ("Status code is not correct", statusCode, equalTo(200));
        // Проверка что информация о заказах получена
        assertThat("Information about orders has not been received", orderCreated, is(true));
        // Проверка что список заказов не пустой
        assertThat("Orders list empty", ordersList, is(not(0)));
    }

    @Test
    @Description("Получение списка заказов не авторизованного пользователя")
    public void orderUserInfoCantBeGetNonAuthUser (){

        bearerToken = "";
        // Информация о заказах пользователя
        ValidatableResponse orderInfo = orderClient.userOrderInfo(bearerToken);
        // Получение статус кода запроса
        int statusCode = orderInfo.extract().statusCode();
        // Получение ответа
        boolean orderInfoNotGet = orderInfo.extract().path("success");
        // Получение сообщения об ошибке
        String errorMessage = orderInfo.extract().path("message");

        // Проверка что статус код соответсвует ожиданиям
        assertThat ("Status code is incorrect", statusCode, equalTo(401));
        // Проверка что информация о заказах не получена
        assertThat("Information about orders has not been received", orderInfoNotGet, is(false));
        // Проверка что сообщение об ошибке соответствует ожиданиям
        assertEquals("The error message is not correct", "You should be authorised", errorMessage);
    }
}
