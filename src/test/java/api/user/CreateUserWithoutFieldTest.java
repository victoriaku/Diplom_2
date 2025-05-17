package api.user;

import api.BaseApiTest;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.UserModel;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static api.testdata.TestData.*;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static org.hamcrest.Matchers.equalTo;
import static steps.UserSteps.*;

@RunWith(Parameterized.class)
public class CreateUserWithoutFieldTest extends BaseApiTest {
    private final UserModel user;
    private Response response;

    public CreateUserWithoutFieldTest(UserModel user) {
        this.user = user;
    }

    @Parameterized.Parameters (name = "Тестовые данные: {0}")
    public static Object[] userData(){
        return new Object[] {
                new UserModel("", getTestUserPassword(), getTestUserName()),
                new UserModel(getTestUserEmail(), "", getTestUserName()),
                new UserModel(getTestUserEmail(), getTestUserPassword(), "")
        };
    }

    @Test
    @DisplayName("Создание пользователя без заполнения одного из обязательных полей")
    public void createUserWithoutFieldFails(){
        response = createUser(user);
        response
                .then()
                .statusCode(HTTP_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void cleanUp(){
        String token = getAccessToken(response);
        if (token != null){
            removeUser(token);
        }
    }
}
