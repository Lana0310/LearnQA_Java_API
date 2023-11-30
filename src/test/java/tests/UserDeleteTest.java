package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Delete cases")
@Feature("Delete user")
public class UserDeleteTest extends BaseTestCase {
    ApiCoreRequests apiCoreRequests=new ApiCoreRequests();

    @Test
    @Description("Test delete user positive")
    @DisplayName("Test create user then delete")
    public void testDeleteUser()
    {
        //create MyUser
        Map<String,String> userData= DataGenerator.getRegistrationData();
        Response responseCreate =apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user",userData) ;
        Assertions.assertResponseCodeEquals(responseCreate,200);
        Assertions.assertJsonHasKey(responseCreate,"id");
        int userId=responseCreate.jsonPath().getInt("id");
        //login MyUser
        Response responseLogin =apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/login",userData);
        Assertions.assertJsonHasKey(responseLogin,"user_id");

        String url="https://playground.learnqa.ru/api/user/"+userId;
        //Delete MyUser
        Response responseDelete =apiCoreRequests.makeDeleteRequest(
                url,
                this.getHeader(responseLogin,"x-csrf-token"),
                this.getCookie(responseLogin,"auth_sid"));
        Assertions.assertResponseCodeEquals(responseDelete,200);
        //Get MyUser info about user id=
        Response responseAuthGetAfterDelete =apiCoreRequests.makeGetRequests(
                url,
                this.getHeader(responseLogin,"x-csrf-token"),
                this.getCookie(responseLogin,"auth_sid"));
        String[] expectedFields={"firstName","lastName","email","username"};
        Assertions.assertJsonHasNotFields(responseAuthGetAfterDelete,expectedFields);
    }

    @Test
    @Description("Test delete user id=2")
    @DisplayName("Test delete superuser")
    public void testDeleteSuperUser()
    {
        Map<String,String> userData=new HashMap<>();
        userData.put("email","vinkotov@example.com");
        userData.put("password","1234");
        //login
        Response responseLogin =apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/login",userData);
        Assertions.assertJsonHasKey(responseLogin,"user_id");
        int userId=responseLogin.jsonPath().getInt("user_id");

        String url="https://playground.learnqa.ru/api/user/"+userId;
        //Delete
        Response responseDelete =apiCoreRequests.makeDeleteRequest(
                url,
                this.getHeader(responseLogin,"x-csrf-token"),
                this.getCookie(responseLogin,"auth_sid"));

        Assertions.assertResponseCodeEquals(responseDelete,400);
        //Get MyUser info about 2
        Response responseAuth =apiCoreRequests.makeGetRequests(
                url,
                this.getHeader(responseLogin,"x-csrf-token"),
                this.getCookie(responseLogin,"auth_sid"));
        Assertions.assertJsonByName(responseAuth,"email",userData.get("email"));
        Assertions.assertJsonByName(responseAuth,"id","2");
    }

    @Test
    @Description("Test delete another user")
    @DisplayName("Test delete another user")
    public void testDeleteAnotherUser()
    {
        //create MyUserForDelete
        Map<String,String> userDataForDelete= DataGenerator.getRegistrationData();
        userDataForDelete.put("username","UserDataForDelete");
        Response responseCreateUserForDelete =apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user",userDataForDelete) ;
        Assertions.assertResponseCodeEquals(responseCreateUserForDelete,200);
        Assertions.assertJsonHasKey(responseCreateUserForDelete,"id");
        int userIdForDelete=responseCreateUserForDelete.jsonPath().getInt("id");

        //create MyUserForLogin
        Map<String,String> userDataForLogin= DataGenerator.getRegistrationData();
        userDataForLogin.put("username","UserDataForLogin");
        Response responseCreateUserForLogin =apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user",userDataForLogin) ;
        Assertions.assertResponseCodeEquals(responseCreateUserForLogin,200);
        Assertions.assertJsonHasKey(responseCreateUserForLogin,"id");
        int userIdForLogin=responseCreateUserForLogin.jsonPath().getInt("id");

        Map<String,String> userDataLogin=new HashMap<>();
        userDataLogin.put("email",userDataForLogin.get("email"));
        userDataLogin.put("password",userDataForLogin.get("password"));

        //login MyUserForLogin
        Response responseLogin =apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/login",userDataLogin);
        Assertions.assertJsonHasKey(responseLogin,"user_id");
        Assertions.assertJsonByName(responseLogin,"user_id",userIdForLogin);

        String urlForDelete="https://playground.learnqa.ru/api/user/" + userIdForDelete;
        String urlForGet="https://playground.learnqa.ru/api/user/" + userIdForLogin;
        System.out.println(urlForGet);
        System.out.println(urlForDelete);

        //Try to Delete MyUserForDelete, MyUserForLogin token
        Response responseDelete =apiCoreRequests.makeDeleteRequest(
                urlForDelete,
                this.getHeader(responseLogin,"x-csrf-token"),
                this.getCookie(responseLogin,"auth_sid"));
        Assertions.assertResponseCodeEquals(responseDelete,200);

        //Get info about MyUserForLogin, deleted defacto
        Response responseGetInfoUserForLogin =apiCoreRequests.makeGetRequests(
                urlForGet,
                this.getHeader(responseLogin,"x-csrf-token"),
                this.getCookie(responseLogin,"auth_sid"));
        Assertions.assertJsonHasNotKey(responseGetInfoUserForLogin,"username");
        Assertions.assertResponseTextContains(responseGetInfoUserForLogin,"User not found");

        //Get info about MyUserForDelete, exist
        Response responseGetInfoUserForDelete =apiCoreRequests.makeGetRequests(
                urlForDelete,
                this.getHeader(responseLogin,"x-csrf-token"),
                this.getCookie(responseLogin,"auth_sid"));
        Assertions.assertJsonByName(responseGetInfoUserForDelete,"username", userDataForDelete.get("username"));
    }
}
