import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.StringUtils;
import org.example.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Epic("Login user/Логин пользователя")
public class UserLoginTest {
    private static final String MESSAGE_UNAUTHORIZED = "email or password are incorrect/неверные email или пароль";
    private ValidatableResponse response;
    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        user = CreateRandomUser.getRandomUser();
        userClient = new UserClient();
    }

    @After
    public void clearState() {
        userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));
    }

    @Test
    @DisplayName("User login by valid credentials/Логин под существующим пользователем")
    public void userLoginByValidCredentials() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        response = userClient.loginUser(user, accessToken);
        int statusCode = response.extract().statusCode();
        boolean isLogin = response.extract().path("success");

        assertThat("Token is null", accessToken, notNullValue());
        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("User is login incorrect", isLogin, equalTo(true));
    }

    @Test
    @DisplayName("User login is empty email/Логин с пустым полем email")
    public void userLoginByEmptyEmail() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        user.setEmail(null);
        response = userClient.loginUser(user, accessToken);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isLogin = response.extract().path("success");

        assertThat("Token is null", accessToken, notNullValue());
        assertThat("Code not equal", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Message not equal", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("User is login correct", isLogin, equalTo(false));
    }

    @Test
    @DisplayName("User login is empty password/Логин с пустым полем пароль")
    public void userLoginByEmptyPassword() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        user.setPassword(null);
        response = userClient.loginUser(user, accessToken);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isLogin = response.extract().path("success");

        assertThat("Token is null", accessToken, notNullValue());
        assertThat("Code not equal", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Message not equal", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("User is login correct", isLogin, equalTo(false));
    }

    @Test
    @DisplayName("User login is error email/Логин с несуществующим email")
    public void userLoginByErrorEmail() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        user.setEmail("praktikum");
        response = userClient.loginUser(user, accessToken);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isLogin = response.extract().path("success");

        assertThat("Token is null", accessToken, notNullValue());
        assertThat("Code not equal", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Message not equal", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("User is login correct", isLogin, equalTo(false));
    }

    @Test
    @DisplayName("User login is error password/Логин с неверным паролем")
    public void userLoginByErrorPassword() {
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
        user.setPassword("praktikum@yandex.ru");
        response = userClient.loginUser(user, accessToken);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isLogin = response.extract().path("success");

        assertThat("Token is null", accessToken, notNullValue());
        assertThat("Code not equal", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("Message not equal", message, equalTo(MESSAGE_UNAUTHORIZED));
        assertThat("User is login correct", isLogin, equalTo(false));
    }
}