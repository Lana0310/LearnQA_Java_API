package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Edit data cases")
@Feature("PutNewData")
public class UserEditTests extends BaseTestCase {
    ApiCoreRequests apiCoreRequests=new ApiCoreRequests();

    @Test
    @Issue(value = "JIRA-4627")
    @TmsLinks({@TmsLink(value = "TL-135")})
    @Owner(value = "Пупкин Валерий Иванович")
    @Severity(value = SeverityLevel.BLOCKER)
    @Description("This positive test for edit user")
    @DisplayName("Edit user data")
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

    @Test
    @Owner(value = "Пупкин Валерий Иванович")
    @Severity(value = SeverityLevel.MINOR)
    @Description("Test edit user data by not authorized user")
    @DisplayName("Test edit info not authorized")
    public void testEditNotAuthorized() {
        //Create
        Map<String,String> userData=DataGenerator.getRegistrationData();
        Response responseCreate =apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user",userData) ;

        Assertions.assertResponseCodeEquals(responseCreate,200);
        Assertions.assertJsonHasKey(responseCreate,"id");
        int userId=responseCreate.jsonPath().getInt("id");
        //Edit
        Map<String,String> userEditData=DataGenerator.getRegistrationData();
        userEditData.put("username","putuser");
        String urlEdit="https://playground.learnqa.ru/api/user/"+userId;
        Response response =apiCoreRequests.makePutRequest(
                urlEdit,userEditData) ;
        Assertions.assertResponseCodeEquals(response,400);
    }

    @Test
    @Owner(value = "Пупкин Валерий Иванович")
    @Severity(value = SeverityLevel.NORMAL)
    @Description("Test edit user data by another user")
    @DisplayName("Test edit info another user")
    public void testEditAnotherUser() {
        //Create
        Map<String,String> userDataForLogin= DataGenerator.getRegistrationData();
        Response responseCreate =apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user",userDataForLogin) ;
        Assertions.assertResponseCodeEquals(responseCreate,200);
        Assertions.assertJsonHasKey(responseCreate,"id");
        int userId=responseCreate.jsonPath().getInt("id");

        //Login
        Map<String,String> authData=new HashMap<>();
        authData.put("email",userDataForLogin.get("email"));
        authData.put("password",userDataForLogin.get("password"));
        Response responseLogin = apiCoreRequests
                .makePostRequests("https://playground.learnqa.ru/api/user/login",authData);
        Assertions.assertJsonHasKey(responseLogin,"user_id");
        responseLogin.prettyPrint();

        //Edit
        Map<String,String> userDataEdit=new HashMap<>();
        userDataEdit.put("username","TestforEdit");
        String urlEdit="https://playground.learnqa.ru/api/user/2";
        Response responseEdit =apiCoreRequests.makePutRequest(urlEdit,userDataEdit,
                this.getHeader(responseLogin,"x-csrf-token"),
                this.getCookie(responseLogin,"auth_sid"));
        responseEdit.prettyPrint();
        String url="https://playground.learnqa.ru/api/user/2";
        Response responseAuth =apiCoreRequests.makeGetRequests(
                url,
                this.getHeader(responseLogin,"x-csrf-token"),
                this.getCookie(responseLogin,"auth_sid"));
        responseAuth.prettyPrint();
        Assertions.assertJsonByName(responseAuth,"username","Vitaliy");
    }

    @Test
    @Owner(value = "Сидоров Валерий Иванович")
    @Severity(value = SeverityLevel.CRITICAL)
    @Description("Test edit user email")
    @DisplayName("Test edit user email without @")
    public void testEditEmailUser() {
        //Create
        Map<String,String> userData= DataGenerator.getRegistrationData();
        Response responseCreate =apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user",userData) ;
        Assertions.assertResponseCodeEquals(responseCreate,200);
        Assertions.assertJsonHasKey(responseCreate,"id");
        int userId=responseCreate.jsonPath().getInt("id");

        //Login
        Map<String,String> authData=new HashMap<>();
        authData.put("email",userData.get("email"));
        authData.put("password",userData.get("password"));
        Response responseLogin = apiCoreRequests
                .makePostRequests("https://playground.learnqa.ru/api/user/login",authData);
        Assertions.assertJsonHasKey(responseLogin,"user_id");

        //Edit
        Map<String,String> userDataEdit= new HashMap<>();
        userDataEdit.put("email","TestforEditmail.ru");
        String urlEdit="https://playground.learnqa.ru/api/user/"+userId;
        Response responseEdit =apiCoreRequests.makePutRequest(
                urlEdit,userDataEdit,
                this.getHeader(responseLogin,"x-csrf-token"),
                this.getCookie(responseLogin,"auth_sid"));
        Assertions.assertResponseCodeEquals(responseEdit,400);
        Assertions.assertResponseTextContains(responseEdit,"Invalid email format");
    }

    @Test
    @Owner(value = "Сидоров Валерий Иванович")
    @Severity(value = SeverityLevel.MINOR)
    @Description("Test edit user firstname")
    @DisplayName("Test edit user firstname too short")
    public void testEditFirstnameUser() {
        //Create
        Map<String,String> userData= DataGenerator.getRegistrationData();
        Response responseCreate =apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user",userData) ;
        Assertions.assertResponseCodeEquals(responseCreate,200);
        Assertions.assertJsonHasKey(responseCreate,"id");
        int userId=responseCreate.jsonPath().getInt("id");

        //Login
        Map<String,String> authData=new HashMap<>();
        authData.put("email",userData.get("email"));
        authData.put("password",userData.get("password"));
        Response responseLogin = apiCoreRequests
                .makePostRequests("https://playground.learnqa.ru/api/user/login",authData);
        Assertions.assertJsonHasKey(responseLogin,"user_id");

        //Edit
        Map<String,String> userDataEdit= new HashMap<>();
        userDataEdit.put("firstName","y");
        String urlEdit="https://playground.learnqa.ru/api/user/"+userId;
        Response responseEdit =apiCoreRequests.makePutRequest(
                urlEdit,userDataEdit,
                this.getHeader(responseLogin,"x-csrf-token"),
                this.getCookie(responseLogin,"auth_sid"));
        Assertions.assertResponseCodeEquals(responseEdit,400);
        Assertions.assertJsonByName(responseEdit,"error","Too short value for field firstName");
    }
};
