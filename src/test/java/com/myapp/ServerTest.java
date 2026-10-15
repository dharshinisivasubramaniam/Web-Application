package com.myapp;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServerTest {
    private MainServer server;

    @BeforeEach
    void setUp() throws IOException{
        server = new MainServer(8080);
        server.start(); // This should start the server before the test
    }


@AfterEach
void tearDown() {
    server.stop();
}

@Test
void healthEndpointReturns200() throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/api/health"))
            .GET()
            .build();

     // This will attempt to send the request
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    assertEquals(200,response.statusCode(),"Health endpoint should return 200 OK");
    assertEquals("OK",response.body(),"Health endpoint should return 'OK'");
}

@Test
void staticFileEndpointReturnsHtml() throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/index.html"))
            .GET()
            .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode(), "Should return 20 OK for static files");



            //LOGIN CODE
            // // Assert that the body contains the HTML we wrote
            // org.junit.jupiter.api.Assertions.assertTrue(
            //     response.body().contains("Register Account"),
            //     "Response should contain the HTML content"
            // );
            
}

    @Test
    void registrationEndpointReturns201() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        
        // Create a unique email for every test run
        String uniqueEmail = "newuser" + System.currentTimeMillis() + "%40test.com";
        String formData = "name=Test+User&phone=5551234&email=" + uniqueEmail + "&password=MySecretPassword";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/register"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // We expect a 201 Created status
        assertEquals(201, response.statusCode(), "Endpoint should successfully register the user");
    }



    //LOGIN CODE
    @Test 
    void loginEndpointScenarios() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

         // 1. Register a test user first so we have someone to log in as
         String uniqueEmail = "loginuser" + System.currentTimeMillis() + "%40test.com";
         String regData = "name=Login+User&phone=555&email=" + uniqueEmail + "&password=CorrectPassword";
         HttpRequest regReq = HttpRequest.newBuilder()
                 .uri(URI.create("http://localhost:8080/api/register"))
                 .header("Content-Type", "application/x-www-form-urlencoded")
                 .POST(HttpRequest.BodyPublishers.ofString(regData))
                 .build();
        client.send(regReq, HttpResponse.BodyHandlers.ofString());

        // 2. Test successful login
        String loginData = "email=" + uniqueEmail + "&password=CorrectPassword";
        HttpRequest loginReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(loginData))
                .build();
        HttpResponse<String> loginRes = client.send(loginReq,HttpResponse.BodyHandlers.ofString());

        org.junit.jupiter.api.Assertions.assertEquals(200,
            loginRes.statusCode(), "Valid login should return 200");
        org.junit.jupiter.api.Assertions.assertEquals("", loginRes.body(),
        "Response body must be completely empty for 200");

        // 3. Test invalid password
        String badPassData = "email=" + uniqueEmail + "&password=Wrongpassword";
        HttpRequest badPassReq = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/login"))
                .header("Content-Type" , "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(badPassData))
                .build();
        HttpResponse<String> badPassRes = client.send(badPassReq,HttpResponse.BodyHandlers.ofString());

        org.junit.jupiter.api.Assertions.assertEquals(401,
            badPassRes.statusCode(),"Invaild password should return 401");
        org.junit.jupiter.api.Assertions.assertEquals("", badPassRes.body(),
    "Response body must be completely empty for 401");

    }
}

