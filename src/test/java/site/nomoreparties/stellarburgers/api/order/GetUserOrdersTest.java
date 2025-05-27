package site.nomoreparties.stellarburgers.api.order;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.BaseTest;
import site.nomoreparties.stellarburgers.models.UserModel;

import java.util.Collections;
import java.util.Random;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static site.nomoreparties.stellarburgers.steps.OrderSteps.*;
import static site.nomoreparties.stellarburgers.steps.UserSteps.*;
import static site.nomoreparties.stellarburgers.testdata.TestData.*;

public class GetUserOrdersTest extends BaseTest {
    private String token;
    private final int ordersQuantity = new Random().nextInt(5) + 1;

    @Before
    public void createUserWithOrders(){
        token = getAccessToken(createUser(
                new UserModel(getTestUserEmail(),
                        getTestUserPassword(),
                        getTestUserName())));

        for (int i = 0; i < ordersQuantity; i++){
            createOrder(Collections.singletonMap("ingredients", getRandomIngredientsIds()),
                    token);
        }
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    public void getUserOrdersSuccess(){
        getUserOrders(token)
                .then()
                .statusCode(HTTP_OK)
                .body(matchesJsonSchemaInClasspath("schema/user-orders.json"))
                .body("success", equalTo(true))
                .body("orders.size()", equalTo(ordersQuantity))
                .body("total", equalTo(ordersQuantity))
                .body("totalToday", equalTo(ordersQuantity));
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    public void getUserOrdersWithoutAuthorizationFails(){
        getUserOrders("")
                .then()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void cleanUp(){
        removeUser(token);
    }
}
