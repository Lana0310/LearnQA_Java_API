package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTests extends BaseTestCase {
    @Test
    public void testGetUserDataNotAuth(){
        Response response= RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();
        Assertions.assertJsonHasKey(response,"username");
        Assertions.assertJsonHasNotKey(response,"firstName");
        Assertions.assertJsonHasNotKey(response,"lastName");
        Assertions.assertJsonHasNotKey(response,"email");
    }

    @Test
    public void testGetUserDetailsAuthAsSameUse(){
        String email="vinkotov@example.com";
        Map<String,String> userData=new HashMap<>();
        userData.put("email",email);
        userData.put("password","1234");
        Response responseGetAuth= RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
        String header = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie=this.getCookie(responseGetAuth,"auth_sid");
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token",header)
                .cookie("auth_sid",cookie)
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();
        String[] expectedFilds={"firstName","lastName","email","username"};
        Assertions.assertJsonHasFields(responseUserData,expectedFilds);
    }
}
