package site.nomoreparties.stellarburgers;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.BeforeClass;

import static site.nomoreparties.stellarburgers.constants.EndPoints.BASE_URL;

public class BaseTest {
    @BeforeClass
    public static void setup(){
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }
}
