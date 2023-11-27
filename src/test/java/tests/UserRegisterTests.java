package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTests extends BaseTestCase {
    @Test
    public void testCreateUserWithExistingEmail()
    {
        String email="vinkotov@example.com";
        Map<String,String> userData=new HashMap<>();
        userData.put("email",email);
        userData=DataGenerator.getRegistrationData(userData);

        Response response= RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user")
                .andReturn();
        Assertions.assertResponseCodeEquals(response,400);
        Assertions.assertResponseTextEquals(response,"Users with email '"+email+"' already exists");
    }

    @Test
    public void testCreateUserSuccessfully()
    {

        Map<String,String> userData=DataGenerator.getRegistrationData();

        Response response= RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user")
                .andReturn();
        Assertions.assertResponseCodeEquals(response,200);
        Assertions.assertJsonHasKey(response,"id");
    }
}
