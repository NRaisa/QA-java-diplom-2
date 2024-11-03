import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.StringUtils;
import org.example.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Epic("Create order/Создать заказ")
public class OrderCreateTest {
    private static final String MESSAGE_BAD_REQUEST = "Ingredient id's must be provided/Необходимо указать ингредиенты";
    private ValidatableResponse response;
    private User user;
    private Order order;
    private UserClient userClient;
    private OrderClient orderClient;

    @Before
    public void setUp() {
        user = CreateRandomUser.getRandomUser();
        order = new Order();
        userClient = new UserClient();
        orderClient = new OrderClient();
    }


    @Test
    @DisplayName("Create order by authorization/Создание заказа с ингредиентами авторизованным пользователем")
    public void orderCreateByAuthorization() {
        fillListIngredients();
        response = userClient.createUser(user);
        String accessToken = response.extract().path("accessToken");
        response = userClient.loginUser(user, accessToken);
        response = orderClient.createOrderByAuthorization(order, accessToken);
        int statusCode = response.extract().statusCode();
        boolean isCreate = response.extract().path("success");
        int orderNumber = response.extract().path("order.number");
        String orderId = response.extract().path("order._id");
        response = userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));

        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("Order is create incorrect", isCreate, equalTo(true));
        assertThat("Order number is null", orderNumber, notNullValue());
        assertThat("Order id is null", orderId, notNullValue());
    }

    @Test
    @DisplayName("Create order without authorization/Создание заказа с ингредиентами неавторизованным пользователем")
    public void orderCreateWithoutAuthorization() {
        fillListIngredients();
        response = orderClient.createOrderWithoutAuthorization(order);
        int statusCode = response.extract().statusCode();
        boolean isCreate = response.extract().path("success");
        int orderNumber = response.extract().path("order.number");

        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("Order is create incorrect", isCreate, equalTo(true));
        assertThat("Order number is null", orderNumber, notNullValue());
    }

    private void fillListIngredients() {
        response = orderClient.getAllIngredients();
        List<String> list = response.extract().path("data._id");
        List<String> ingredients = order.getIngredients();
        ingredients.add(list.get(1));
        ingredients.add(list.get(0));
        ingredients.add(list.get(5));
    }

    @Test
    @DisplayName("Create order without authorization and ingredients/ Создание заказа без ингредиентов неавторизованным пользователем")
    public void orderCreateWithoutAuthorizationAndIngredients() {
        response = orderClient.createOrderWithoutAuthorization(order);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreate = response.extract().path("success");

        assertThat("Code not equal", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Message not equal", message, equalTo(MESSAGE_BAD_REQUEST));
        assertThat("Order is create correct", isCreate, equalTo(false));
    }

    @Test
    @DisplayName("Create order without authorization and error hash ingredient/Создание заказа без авторизации и неверным хешем ингредиентов")
    public void orderCreateWithoutAuthorizationAndChangeHashIngredient() {
        response = orderClient.getAllIngredients();
        List<String> list = response.extract().path("data._id");
        List<String> ingredients = order.getIngredients();
        ingredients.add(list.get(777));
        response = orderClient.createOrderWithoutAuthorization(order);
        int statusCode = response.extract().statusCode();

        assertThat("Code not equal", statusCode, equalTo(SC_INTERNAL_SERVER_ERROR));

    }
}


