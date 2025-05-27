package site.nomoreparties.stellarburgers.api.order;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.BaseTest;
import site.nomoreparties.stellarburgers.models.UserModel;
import site.nomoreparties.stellarburgers.steps.UserSteps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static site.nomoreparties.stellarburgers.steps.OrderSteps.createOrder;
import static site.nomoreparties.stellarburgers.steps.OrderSteps.getRandomIngredientsIds;
import static site.nomoreparties.stellarburgers.steps.UserSteps.getAccessToken;
import static site.nomoreparties.stellarburgers.steps.UserSteps.removeUser;
import static site.nomoreparties.stellarburgers.testdata.TestData.*;

public class CreateOrderTest extends BaseTest {
    private String token;
    private Map<String, List<String>> requestBody;

    @Before
    public void createUser(){
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

    @After
    public void cleanUp(){
        removeUser(token);
    }
}
