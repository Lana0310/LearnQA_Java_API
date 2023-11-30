package lib;

import io.restassured.response.Response;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
    public static void assertJsonByName(Response Response,String name,int expectedValue){
        Response.then().assertThat().body("$",hasKey(name));
        int value= Response.jsonPath().getInt(name);
        assertEquals(expectedValue,value,"Json value is not equal to expected value");
    }
    public static void assertJsonByName(Response Response,String name,String expectedValue){
        Response.then().assertThat().body("$",hasKey(name));
        String value= Response.jsonPath().getString(name);
        assertEquals(expectedValue,value,"Json value is not equal to expected value");
    }

    public static void assertResponseTextEquals(Response Response,String expectedValue){
        assertEquals(expectedValue,Response.asString(),"Response text is not expected");
    }
    public static void assertResponseTextContains(Response Response,String expectedValue){
        //assertTrue(Response.asPrettyString().equals(expectedValue),"Response text is not expected");
        assertEquals(true,Response.asString().contains(expectedValue),"Response text is not expected");
    }

    public static void assertResponseCodeEquals(Response Response,int expectedValue){
        assertEquals(expectedValue,Response.statusCode(),"Response statusCode is not expected");
    }

    public static void assertJsonHasKey(Response Response,String expectedValue){
        Response.then().assertThat().body("$",hasKey(expectedValue));
    }
    public static void assertJsonHasNotKey(Response Response,String unexpectedValue){
        Response.then().assertThat().body("$",not(hasKey(unexpectedValue)));
    }

    public static void assertJsonHasFields(Response Response,String[] expectedValues){
        for(String expectedValue:expectedValues) {
            Assertions.assertJsonHasKey(Response,expectedValue);
        }
    }

    public static void assertJsonHasNotFields(Response Response,String[] expectedValues){
        for(String expectedValue:expectedValues) {
            Assertions.assertJsonHasNotKey(Response,expectedValue);
        }
    }
}
