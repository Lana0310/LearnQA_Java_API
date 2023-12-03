package tests;
import io.qameta.allure.*;
import lib.Assertions;
import lib.BaseTestCase;
import lib.ApiCoreRequests;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;

@Epic("Authorization cases")
@Feature("Authorisation")
public class UserAuthTest extends BaseTestCase {
    String cookie;
    String header;
    int userIdOnAuth;
    ApiCoreRequests apiCoreRequests=new ApiCoreRequests();

    @BeforeEach
    public void loginUser(){
        Map<String, String> data = new HashMap<>();
        data.put("email", "vinkotov@example.com");
        data.put("password", "1234");
        Response responseGetAuth = apiCoreRequests
                .makePostRequests("https://playground.learnqa.ru/api/user/login",data);
        this.cookie=this.getCookie(responseGetAuth,"auth_sid");
        this.header=this.getHeader(responseGetAuth,"x-csrf-token");
        this.userIdOnAuth=this.getIntFromJson(responseGetAuth,"user_id");
    }

    @Test
    @Owner(value = "Сидоров Валерий Иванович")
    @Severity(value = SeverityLevel.BLOCKER)
    @Description("This test  successfully authorize user by email and password")
    @DisplayName("Test  positive auth user")
    public void testAuthUser() {
        Response responseCheckAuth=apiCoreRequests
                .makeGetRequests("https://playground.learnqa.ru/api/user/auth",
                this.header,this.cookie);
        Assertions.assertJsonByName(responseCheckAuth,"user_id",this.userIdOnAuth);
    }

    @Owner(value = "Сидоров Валерий Иванович")
    @Severity(value = SeverityLevel.MINOR)
    @Description("This test check authorization status w/o sending auth cookie or header")
    @DisplayName("Test negative auth user")
    @ParameterizedTest
    @ValueSource(strings = {"cookies","headers"})
    public void testNegativeAuthUser(String condition)
    {
        if (condition.equals("cookies")){
            Response resp=apiCoreRequests.makeGetRequestsWithCookie(
                    "https://playground.learnqa.ru/api/user/auth",this.cookie
            );
            Assertions.assertJsonByName(resp,"user_id",0);
        }else if (condition.equals("headers")){
            Response resp=apiCoreRequests.makeGetRequestsWithToken(
                    "https://playground.learnqa.ru/api/user/auth",this.header
            );
            Assertions.assertJsonByName(resp,"user_id",0);
        }else {
            throw new IllegalArgumentException("Condition value is unknown: "+condition);
        }
    }
}
