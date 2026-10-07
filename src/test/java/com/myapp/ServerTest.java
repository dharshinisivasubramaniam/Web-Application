package com.myapp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


import static org.junit.jupiter.api.Assertions.assertEquals;

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

}

