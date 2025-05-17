package api.order;

import api.BaseApiTest;
import io.qameta.allure.junit4.DisplayName;
import models.UserModel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.Random;

import static api.testdata.TestData.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.*;
import static steps.OrderSteps.*;
import static steps.UserSteps.*;

public class GetUserOrdersTest extends BaseApiTest {
    private static String token;
    private static final int ordersQuantity = new Random().nextInt(5) + 1;

    @BeforeClass
    public static void createUserWithOrders(){
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

    @AfterClass
    public static void cleanUp(){
        removeUser(token);
    }
}
