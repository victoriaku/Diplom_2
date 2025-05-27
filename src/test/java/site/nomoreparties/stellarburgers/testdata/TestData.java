package site.nomoreparties.stellarburgers.testdata;

import com.github.javafaker.Faker;

public class TestData {
    private static Faker faker = new Faker();

    public static String getTestUserEmail(){
        return "user_" + System.currentTimeMillis() + "@test.ru";
    }

    public static String getTestUserPassword(){
        return faker.regexify("[a-z]{8}");
    }

    public static String getTestUserName(){
        return faker.name().firstName();
    }
}
