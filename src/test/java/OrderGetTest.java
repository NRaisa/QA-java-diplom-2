import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.StringUtils;
import org.example.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@Epic("Get order/Получение заказа")
public class OrderGetTest {
    private static final String MESSAGE_UNAUTHORIZED = "You should be authorised/Авторизуйтесь";
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
    @DisplayName("Get order by authorization user/Получить заказ авторизованного пользователя")
    public void getOrderByAuthorizationUser() {
        response = userClient.createUser(user);
        String accessToken = response.extract().path("accessToken");
        response = userClient.loginUser(user, accessToken);
        response = orderClient.createOrderByAuthorization(order, accessToken);
        response = orderClient.getOrdersByAuthorization(accessToken);
        int statusCode = response.extract().statusCode();
        boolean isGet = response.extract().path("success");
        response = userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));

        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("Order is get incorrect", isGet, equalTo(true));
    }

    @Test
    @DisplayName("Get order without authorization user/Получить заказ неавторизованного пользователя")
    public void getOrderWithoutAuthorizationUser() {
        response = orderClient.createOrderWithoutAuthorization(order);
        response = orderClient.getOrdersWithoutAuthorization();
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isGet = response.extract().path("success");

        assertThat("Code not equal", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Message not equal", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("Order is get correct", isGet, equalTo(false));
    }

}