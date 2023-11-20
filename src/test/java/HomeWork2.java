import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.Thread;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class HomeWork2 {
    @Test
    public void TestParsigJson() {
        JsonPath response= RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        System.out.println(response.get("messages.message[1]").toString());
        System.out.println(response.get("messages.timestamp[1]").toString());
    }
    @Test
    public void TestRedirect() {
        Response response= RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();
        String location=response.getHeader("location");
        System.out.println(location);
    }
    @Test
    public void TestLongRedirect() {
        int statusCode=0;
        int count=0;
        String location="https://playground.learnqa.ru/api/long_redirect";
        while (statusCode!=200){
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(location)
                    .andReturn();
            location = response.getHeader("location");
            statusCode = response.getStatusCode();
            if(statusCode!=200) count++;
            System.out.println(location);
            System.out.println(statusCode);
        }
        System.out.println("Колличество редиректов "+count);
    }

    @Test
    public void TestToken() {
        JsonPath responseGetToken= RestAssured
                .given()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        String token=responseGetToken.get("token").toString();
        int time=responseGetToken.get("seconds");
        int delay=time*1000;
        Map<String,String> params = new HashMap<>();
        params.put("token",token);
        JsonPath responseNotReady=RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        responseNotReady.prettyPrint();
        if(responseNotReady.get("status").equals("Job is NOT ready")){
            try{
                Thread.sleep(delay);
            }
            catch (Exception e){
                System.out.println(e);
            }
            JsonPath responseGetResult=RestAssured
                    .given()
                    .queryParams(params)
                    .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                    .jsonPath();
            if(responseGetResult.get("result")!=null&&responseGetResult.get("status").equals("Job is ready")) {
                responseGetResult.prettyPrint();
            }
            else System.out.println("Error");
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data1.csv")
    public void TestSearchPassword(String pass)
    {
        Map<String,String> data=new HashMap<>();
        data.put("login","super_admin");
        data.put("password",pass);
        Response response= RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                .andReturn();
        String auth_cookie=response.getCookie("auth_cookie");
        Map<String,String> cookie=new HashMap<>();
        cookie.put("auth_cookie",auth_cookie);
        Response response1= RestAssured
                .given()
                .body(data)
                .cookies(cookie)
                .when()
                .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie").andReturn();
        if(!response1.asString().equals("You are NOT authorized")){System.out.println("Right pass : "+pass);}
        else System.out.print("");
    }

    @ParameterizedTest
    @ValueSource(strings = {"мама мыла раму.","1234567890123456"})
    public void TestForString15(String str)
    {
        assertTrue(str.length()>15,"Текст меньше 15 символов");
    }
    @Test
    public void testForCookies(){
        Response responseGetAuth = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();
        assertEquals(200, responseGetAuth.statusCode(), "Unexpected status code");
        Map<String,String> cookies=new HashMap<>();
        cookies=responseGetAuth.getCookies();
        assertTrue(cookies.containsKey("HomeWork"),"Response doesn't have cookie 'HomeWork'");
        assertEquals("hw_value",cookies.get("HomeWork"),"Unexpected value for cookie 'HomeWork'");
    }

    @Test
    public void testForHeaders(){
        Map<String, String> data = new HashMap<>();
        Response responseGetAuth = RestAssured
                .given()
                .get(" https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        assertEquals(200, responseGetAuth.statusCode(), "Unexpected status code");
        Headers headers=responseGetAuth.getHeaders();
        assertTrue(headers.hasHeaderWithName("x-secret-homework-header"),"Response doesn't have  header 'x-csrf-token header'");
        assertEquals("Some secret value", headers.getValue("x-secret-homework-header"), "Unexpected value for header 'x-secret-homework-header'");
    }

    @ParameterizedTest
    @CsvSource({
            "Mobile, No, Android, 'Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30'",
            "Mobile, Chrome, iOS, 'Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1'",
            "Googlebot, Unknown, Unknown, 'Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)'",
            "Web, Chrome, No, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0'",
            "Mobile, No, iPhone,'Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1'"
    })
    public void testForUserAgent(String platform,String browser,String device,String userAgent){
        Response response = RestAssured
                .given()
                .header("User-Agent",userAgent)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .andReturn();
        assertEquals(platform,response.jsonPath().get("platform"),"Response value platform doesn't equal "+platform);
        assertEquals(browser,response.jsonPath().get("browser"),"Response value browser doesn't equal "+browser);
        assertEquals(device,response.jsonPath().get("device"),"Response value device doesn't equal "+device);
    }
}
