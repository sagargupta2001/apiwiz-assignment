package org.apiwiz.api;

import io.restassured.http.ContentType;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class ApiResourceIntegrationTest {

    @Test
    public void testInvokeHelloEndpoint() {
        String requestJson = """
            {
              "apiMethod": "GET",
              "url": "http://localhost:8080/hello",
              "headerVariables": {
                "Content-Type": "application/json"
              },
              "bodyType": "application/json",
              "requestBody": null,
              "params": []
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestJson)
                .when()
                .post("/api/invoke")
                .then()
                .statusCode(200)
                .body(equalTo("Hello from Quarkus REST"));
    }
}
