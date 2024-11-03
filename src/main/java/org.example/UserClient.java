package org.example;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends Config{

    @Step("Send POST request to /api/auth/register")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .log().all()
                .post(EndPoints.USER_PATH + "register")
                .then()
                .log().all();
    }

    @Step("Send POST request to /api/auth/login")
    public ValidatableResponse loginUser(User user, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .body(user)
                .log().all()
                .post(EndPoints.USER_PATH + "login")
                .then()
                .log().all();
    }


    @Step("Send DELETE request to /api/auth/user")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .log().all()
                .delete(EndPoints.USER_PATH + "user")
                .then()
                .log().all();
    }

}
