package api.user;

import api.BaseApiTest;
import io.qameta.allure.junit4.DisplayName;
import models.UserModel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static api.testdata.TestData.*;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static steps.UserSteps.*;

@RunWith(Parameterized.class)
public class LoginUserFailsTest extends BaseApiTest {

    private static final String EMAIL = getTestUserEmail();
    private static final String PASSWORD = getTestUserPassword();

    private static UserModel user = new UserModel(EMAIL, PASSWORD, getTestUserName());

    private static String tokenAfterCreation;

    private final UserModel invalidUser;


    public LoginUserFailsTest(UserModel invalidUser) {
        this.invalidUser = invalidUser;
    }

    @Parameterized.Parameters (name = "Тестовые данные: {0}")
    public static Object[] invalidUsers(){
        return new Object[]{
                new UserModel("", PASSWORD),
                new UserModel(EMAIL, ""),
                new UserModel(EMAIL, getTestUserPassword()),
                new UserModel(getTestUserEmail(), PASSWORD)
        };
    }

    @BeforeClass
    public static void setUser(){
        tokenAfterCreation = getAccessToken(createUser(user));
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным логином или паролем")
    public void loginUserFails(){
        loginUser(invalidUser)
                .then()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @AfterClass
    public static void cleanUp(){
        user.setName(null);
        String tokenAfterLogin = getAccessToken(loginUser(user));
        removeUser((tokenAfterLogin != null) ? tokenAfterLogin : tokenAfterCreation);
    }
}
