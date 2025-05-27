package site.nomoreparties.stellarburgers.api.user;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.BaseTest;
import site.nomoreparties.stellarburgers.models.UserModel;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static site.nomoreparties.stellarburgers.steps.UserSteps.*;
import static site.nomoreparties.stellarburgers.testdata.TestData.*;

public class LoginUserTest extends BaseTest {
    private final String email = getTestUserEmail();
    private final String password = getTestUserPassword();
    private final String name = getTestUserName();
    private UserModel user = new UserModel(email, password, name);

    private String tokenAfterCreation;

    @Before
    public void setUser(){
        tokenAfterCreation = getAccessToken(createUser(user));
        user.setName(null);
    }

    @Test
    @DisplayName("Авторизация пользователя с валидными данными")
    public void loginUserSuccess(){
        loginUser(user)
                .then()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name));
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным логином")
    public void loginUserWithInvalidLoginFails(){
        checkInvalidLoginResponse(loginUser(new UserModel(getTestUserEmail(), password)));
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным паролем")
    public void loginUserWithInvalidPasswordFails(){
        checkInvalidLoginResponse(loginUser(new UserModel(email, getTestUserPassword())));
    }

    @Test
    @DisplayName("Авторизация пользователя без логина")
    public void loginUserWithoutLoginFails(){
        checkInvalidLoginResponse(loginUser(new UserModel("", password)));
    }

    @Test
    @DisplayName("Авторизация пользователя без пароля")
    public void loginUserWithoutPasswordFails(){
        checkInvalidLoginResponse(loginUser(new UserModel(email, "")));
    }

    @After
    public void cleanUp(){
        String tokenAfterLogin = getAccessToken(loginUser(user));
        removeUser((tokenAfterLogin != null) ? tokenAfterLogin : tokenAfterCreation);
    }
}
