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

            // Assert that the body contains the HTML we wrote
            org.junit.jupiter.api.Assertions.assertTrue(
                response.body().contains("Hello World"),
                "Response should contain the HTML content"
            );
            
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

}

