package site.nomoreparties.stellarburgers.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import site.nomoreparties.stellarburgers.models.UserModel;

import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static site.nomoreparties.stellarburgers.constants.EndPoints.*;

public class UserSteps {

    @Step ("Создать пользователя")
    public static Response createUser(UserModel user){
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(CREATE_USER_PATH);
    }

    @Step ("Получить accessToken пользователя")
    public static String getAccessToken(Response userResponse){
        String accessToken = userResponse
                .then()
                .extract()
                .path("accessToken");
        return accessToken == null ? null : accessToken.replaceFirst("Bearer ", "");
    }

    @Step ("Авторизовать пользователя")
    public static Response loginUser(UserModel user){
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(LOGIN_USER_PATH);
    }

    @Step ("Обновить информацию о пользователе")
    public static Response editUser(String token, UserModel user){
        return given()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .body(user)
                .when()
                .patch(EDIT_USER_PATH);
    }

    @Step ("Удалить пользователя")
    public static Response removeUser(String token){
        return given()
                .auth().oauth2(token)
                .when()
                .delete(REMOVE_USER_PATH);
    }

    @Step ("Проверить ответ при попытке авторизации с неверным логином или паролем")
    public static void checkInvalidLoginResponse(Response loginResponse){
        loginResponse
                .then()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
