package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.*;

import static constants.EndPoints.*;
import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step ("Получить данные об ингредиентах")
    public static Response getIngredients(){
        return given()
                .when()
                .get(GET_INGREDIENTS_PATH);
    }

    @Step ("Получить список идентификаторов всех ингредиентов")
    public static List<String> getIngredientsIds(){
        return getIngredients()
                .then()
                .extract()
                .path("data._id");
    }

    @Step ("Получить список идентификаторов случайных 1-10 ингредиентов")
    public static List<String> getRandomIngredientsIds(){
        List<String> ingredientsIds = getIngredientsIds();
        int totalQuantity = ingredientsIds.size();
        if (totalQuantity == 0) {
            return Collections.emptyList();
        }

        Random random = new Random();
        int randomQuantity = random.nextInt(Math.min(totalQuantity, 10)) + 1;

        Set<String> randomIds = new HashSet<>();
        while (randomIds.size() < randomQuantity) {
            randomIds.add(ingredientsIds.get(random.nextInt(totalQuantity)));
        }
        return new ArrayList<>(randomIds);
    }

    @Step ("Создать заказ")
    public static Response createOrder(Map<String, List<String>> ingredients, String token){
        return given()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .body(ingredients)
                .when()
                .post(CREATE_ORDER_PATH);
    }

    @Step ("Получить заказы конкретного пользователя")
    public static Response getUserOrders(String token){
        return given()
                .auth().oauth2(token)
                .when()
                .get(GET_USER_ORDERS_PATH);
    }

}
