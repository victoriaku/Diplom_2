package api.user;

import api.BaseApiTest;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.UserModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static api.testdata.TestData.*;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.equalTo;
import static steps.UserSteps.*;

public class EditUserTest extends BaseApiTest {
    private final String initEmail = getTestUserEmail();
    private final String initPassword = getTestUserPassword();
    private final String initName = getTestUserName();

    private final UserModel user = new UserModel(initEmail, initPassword, initName);

    private List<String> tokens = new ArrayList<>();

    @Before
    public void createTestUser(){
        tokens.add(getAccessToken(createUser(user)));
    }

    @Test
    @DisplayName("Изменение почты пользователя")
    public void editUserEmailSuccess(){
        String newEmail = getTestUserEmail();
        UserModel newUser = new UserModel(newEmail, initPassword, initName);

        editUser(tokens.get(0), newUser)
                .then()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(initName));
    }

    @Test
    @DisplayName("Изменение имени пользователя")
    public void editUserNameSuccess(){
        String newName = getTestUserName();
        UserModel editedUser = new UserModel(initEmail, initPassword, newName);

        editUser(tokens.get(0), editedUser)
                .then()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(initEmail))
                .body("user.name", equalTo(newName));
    }

    @Test
    @DisplayName("Изменение пароля пользователя")
    public void editUserPasswordSuccess(){
        String newPassword = getTestUserPassword();
        UserModel editedUser = new UserModel(initEmail, newPassword, initName);

        editUser(tokens.get(0), editedUser)
                .then()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(initEmail))
                .body("user.name", equalTo(initName));


        editedUser.setName(null);
        Response loginResponse = loginUser(editedUser);

        String loginToken = getAccessToken(loginResponse);
        if (loginToken != null)
        {
            tokens.set(0, loginToken);
        }

        loginResponse
                .then()
                .statusCode(HTTP_OK)
                .body("success", equalTo(true));
    }


    @Test
    @DisplayName("Изменение пользователя без авторизации")
    public void editUserWithoutTokenFails(){
        editUser("", user)
                .then()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение почты пользователя на уже занятую другим пользователем")
    public void editUserWithDuplicateEmailFails(){
        UserModel newUser = new UserModel(getTestUserEmail(), initPassword, initName);
        String newUserToken = getAccessToken(createUser(newUser));
        tokens.add(newUserToken);

        editUser(newUserToken, user)
                .then()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"));
    }

    @After
    public void cleanUp(){
        for (String token: tokens){
            removeUser(token);
        }
    }
}
