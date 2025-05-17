package api.user;

import api.BaseApiTest;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.UserModel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static api.testdata.TestData.*;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static steps.UserSteps.*;

public class LoginUserSuccessTest extends BaseApiTest {
    private static final String EMAIL = getTestUserEmail();
    private static final String PASSWORD = getTestUserPassword();
    private static final String NAME = getTestUserName();

    private static UserModel user = new UserModel(EMAIL, PASSWORD, NAME);

    private static String tokenAfterCreation;

    private static Response response;

    @BeforeClass
    public static void setUser(){
        tokenAfterCreation = getAccessToken(createUser(user));
        user.setName(null);
    }

    @Test
    @DisplayName("Авторизация пользователя с валидными данными")
    public void loginUserSuccess(){
        response = loginUser(user);
        response
                .then()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(EMAIL))
                .body("user.name", equalTo(NAME));
    }

    @AfterClass
    public static void cleanUp(){
        String tokenAfterLogin = getAccessToken(response);
        removeUser((tokenAfterLogin != null) ? tokenAfterLogin : tokenAfterCreation);
    }
}
