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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Registration cases")
@Feature("Registration")
public class UserRegisterTests extends BaseTestCase {
    ApiCoreRequests apiCoreRequests=new ApiCoreRequests();
    @Test
    @Description("This negative test, user is already exist ")
    @DisplayName("Test negative create user")
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
    @Description("This test  successfully create new user")
    @DisplayName("Test  positive create user")
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

    @Test
    @Description("This negative test, incorrect email")
    @DisplayName("Test negative create user email without @")
    public void testCreateUserWithIncorrectEmail()
    {

        Map<String,String> userData=DataGenerator.getRegistrationData();
        userData.put("email","mytestmail.com");
        Response response =apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user",userData) ;
        Assertions.assertResponseCodeEquals(response,400);
        Assertions.assertResponseTextEquals(response,"Invalid email format");
    }

    @Test
    @Description("This negative test, very short name")
    @DisplayName("Test negative create user name 1 symbol")
    public void testCreateUserWithShortName()
    {

        Map<String,String> userData=DataGenerator.getRegistrationData();
        userData.put("firstName","Y");
        Response response =apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user",userData) ;
        Assertions.assertResponseCodeEquals(response,400);
        Assertions.assertResponseTextEquals(response,"The value of 'firstName' field is too short");
    }

    @Test
    @Description("This negative test, long name")
    @DisplayName("Test negative create user name 255 symbol")
    public void testCreateUserWithLongName()
    {

        Map<String,String> userData=DataGenerator.getRegistrationData();
        userData.put("firstName",
                "gfhjhhgjbjbdskcdlhjhjhjhbjhbchbchjbhjbhjdfkjlsbjhfljfjdlcfjlekvrprmgrmfmfmrefmrefrefjcdnsjcnsdjncsncidsvidjflkvmldfkmvklfdmvklmvkldmvkldmkldmvklmdfklvmdflkvdlkdfjiijfievmdvlknvuiirevureviorjiovjremvlkmfklvmdfklvmfkdlvmkdflmvldfkmvklfdvldkvmervnirovnfdnvjkd");
        Response response =apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user",userData) ;
        Assertions.assertResponseCodeEquals(response,400);
        Assertions.assertResponseTextEquals(response,"The value of 'firstName' field is too long");
    }

    @Description("This negative test, some field is empty")
    @DisplayName("Test negative create user without field")
    @ParameterizedTest
    @ValueSource(strings = {"email","password","username","firstName","lastName"})
    public void testCreateUserWithoutSomeField(String emptyField)
    {
        Map<String,String> userData=DataGenerator.getRegistrationData();
        userData.put(emptyField,null);
        Response response =apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user",userData) ;
        Assertions.assertResponseCodeEquals(response,400);
        Assertions.assertResponseTextEquals(response,"The following required params are missed: "+emptyField);
    }
}
