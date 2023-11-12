import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;


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
}
