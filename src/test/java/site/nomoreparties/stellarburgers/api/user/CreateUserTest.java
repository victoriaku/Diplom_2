package site.nomoreparties.stellarburgers.api.user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import site.nomoreparties.stellarburgers.BaseTest;
import site.nomoreparties.stellarburgers.models.UserModel;

import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static site.nomoreparties.stellarburgers.steps.UserSteps.*;
import static site.nomoreparties.stellarburgers.testdata.TestData.*;

public class CreateUserTest extends BaseTest {

    private final String email = getTestUserEmail();
    private final String password = getTestUserPassword();
    private final String name = getTestUserName();
    private final UserModel user = new UserModel(email, password, name);

    private List<Response> responses = new ArrayList<>();

    @Test
    @DisplayName("Создание пользователя с валидными данными")
    public void createUserSuccess(){
        responses.add(createUser(user));
        responses.get(0)
                .then()
                .statusCode(HTTP_CREATED)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void createDuplicateUserFails(){
        responses.add(createUser(user));
        responses.add(createUser(user));
        responses.get(1)
                .then()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @After
    public void cleanUp(){
        for (Response response: responses){
            String token = getAccessToken(response);
            if (token != null){
                removeUser(token);
            }
        }
    }
}
