package api.order;

import api.BaseApiTest;
import io.qameta.allure.junit4.DisplayName;
import models.UserModel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import steps.UserSteps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static api.testdata.TestData.*;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static steps.OrderSteps.createOrder;
import static steps.OrderSteps.getRandomIngredientsIds;
import static steps.UserSteps.getAccessToken;
import static steps.UserSteps.removeUser;

public class CreateOrderTest extends BaseApiTest {
    private static String token;
    private Map<String, List<String>> requestBody;

    @BeforeClass
    public static void createUser(){
        token = getAccessToken(UserSteps.createUser(
                new UserModel(getTestUserEmail(),
                getTestUserPassword(),
                getTestUserName())));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void createOrderWithAuthorizationSuccess(){
        requestBody = Collections.singletonMap("ingredients", getRandomIngredientsIds());

        createOrder(requestBody, token)
                .then()
                .statusCode(HTTP_CREATED)
                .body("name", notNullValue())
                .body("order.number", notNullValue())
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuthorizationSuccess(){
        requestBody = Collections.singletonMap("ingredients", getRandomIngredientsIds());

        createOrder(requestBody, "")
                .then()
                .statusCode(HTTP_CREATED)
                .body("name", notNullValue())
                .body("order.number", notNullValue())
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без указания ингредиентов")
    public void createOrderWithoutIngredientsFails(){
        requestBody = Collections.singletonMap("ingredients", Collections.emptyList());

        createOrder(requestBody, token)
                .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientHashFails(){
        requestBody = Collections.singletonMap("ingredients", List.of("invalid_ingredient_hash"));

        createOrder(requestBody, token)
                .then()
                .statusCode(HTTP_INTERNAL_ERROR);
    }

    @AfterClass
    public static void cleanUp(){
        removeUser(token);
    }
}
