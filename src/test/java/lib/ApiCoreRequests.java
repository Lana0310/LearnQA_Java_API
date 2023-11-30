package lib;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {
    @Step("Make a GET-request with token and auth cookie")
    public Response makeGetRequests(String url, String token, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token",token))
                .cookie("auth_sid",cookie)
                .get(url)
                .andReturn();
    }
    @Step("Make a GET-request with auth cookie only")
    public Response makeGetRequestsWithCookie(String url, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid",cookie)
                .get(url)
                .andReturn();
    }
    @Step("Make a GET-request with token only")
    public Response makeGetRequestsWithToken(String url, String token){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token",token))
                .get(url)
                .andReturn();
    }
    @Step("Make a POST-request")
    public Response makePostRequests(String url, Map<String, String> data ){
        return given()
                .filter(new AllureRestAssured())
                .body(data)
                .post(url)
                .andReturn();
    }

    @Step("Make a PUT-request")
    public Response makePutRequest(String url, Map<String, String> data ){
        return given()
                .filter(new AllureRestAssured())
                .body(data)
                .put(url)
                .andReturn();
    }

    @Step("Make a GET-request with token and auth cookie")
    public Response makePutRequest(String url,Map<String, String> data, String token, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token",token))
                .cookie("auth_sid",cookie)
                .body(data)
                .put(url)
                .andReturn();
    }

    @Step("Make a Delete-request with token and auth cookie")
    public Response makeDeleteRequest(String url, String token, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token",token))
                .cookie("auth_sid",cookie)
                .delete(url)
                .andReturn();
    }
    @Step("Make a Delete-request with token and auth cookie")
    public Response makeDeleteRequest(String url){
        return given()
                .filter(new AllureRestAssured())
                .delete(url)
                .andReturn();
    }
}
