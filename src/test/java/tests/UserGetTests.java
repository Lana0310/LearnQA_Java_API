package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Get info cases")
@Feature("GetUserData")
public class UserGetTests extends BaseTestCase {
    ApiCoreRequests apiCoreRequests=new ApiCoreRequests();
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

    @Test
    @Description("Test get user data by another user")
    @DisplayName("Test get info another user")
    public void testGetInfoByAnotherUser()
    {
        //create MyUser
        Map<String,String> userData= DataGenerator.getRegistrationData();
        Response responseCreate =apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user",userData) ;
        Assertions.assertResponseCodeEquals(responseCreate,200);
        Assertions.assertJsonHasKey(responseCreate,"id");

        //login MyUser
        Response responseLogin =apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/login",userData);
        Assertions.assertJsonHasKey(responseLogin,"user_id");

        //Get MyUser info about user id=2
        String url="https://playground.learnqa.ru/api/user/"+"2";
        Response responseAuth =apiCoreRequests.makeGetRequests(
                url,
                this.getHeader(responseLogin,"x-csrf-token"),
                this.getCookie(responseLogin,"auth_sid"));
        Assertions.assertJsonHasKey(responseAuth,"username");
        String[] expectedFields={"firstName","lastName","email"};
        Assertions.assertJsonHasNotFields(responseAuth,expectedFields);

    }
}
