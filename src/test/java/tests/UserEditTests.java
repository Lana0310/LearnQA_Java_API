package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTests extends BaseTestCase {
    @Test
    public void testEditJustCreated(){
        //Generate
        Map<String,String> userData= DataGenerator.getRegistrationData();
        JsonPath  responseCreat= RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user")
                .jsonPath();
        String userId=responseCreat.getString("id");
        //login
        Map<String,String> authData=new HashMap<>();
        authData.put("email",userData.get("email"));
        authData.put("password",userData.get("password"));
        Response responseAuth= RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
        //edit
        String newName="Changename";
        Map<String,String> editData=new HashMap<>();
        editData.put("firstName",newName);
        Response responseEdit= RestAssured
                .given()
                .header("x-csrf-token",this.getHeader(responseAuth,"x-csrf-token"))
                .cookie("auth_sid",responseAuth.cookie("auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/"+userId)
                .andReturn();
        //Get new details
        Response responseUserData= RestAssured
                .given()
                .header("x-csrf-token",this.getHeader(responseAuth,"x-csrf-token"))
                .cookie("auth_sid",responseAuth.cookie("auth_sid"))
                .get("https://playground.learnqa.ru/api/user/"+userId)
                .andReturn();
        Assertions.assertJsonByName(responseUserData,"firstName",newName);
    }
};
