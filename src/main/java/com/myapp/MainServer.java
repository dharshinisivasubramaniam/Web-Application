package com.myapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpServer;

public class MainServer {

    private final HttpServer server;

    public MainServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // 1. Health endpoint
        server.createContext("/api/health", exchange -> {
            String response = "OK";
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) { os.write(response.getBytes()); }
        });

        // 2. Registration API Endpoint
        server.createContext("/api/register", exchange -> {
            // Only allow POST requests
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1); 
                return;
            }

            try {
                // Read and parse the form data
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> formData = parseFormData(body);

                String name = formData.get("name");
                String phone = formData.get("phone");
                String email = formData.get("email");
                String password = formData.get("password");

                // Basic Server-Side Validation
                if (name == null || phone == null || email == null || password == null || name.isBlank() || password.isBlank()) {
                    String response = "Missing required fields";
                    exchange.sendResponseHeaders(400, response.length());
                    try (OutputStream os = exchange.getResponseBody()) { os.write(response.getBytes()); }
                    return;
                }

                try (Connection conn = DatabaseManager.getConnection()) {
                    // Check for duplicate email
                    if (UserRepository.emailExists(email, conn)) {
                        String response = "Email already exists";
                        exchange.sendResponseHeaders(409, response.length());
                        try (OutputStream os = exchange.getResponseBody()) { os.write(response.getBytes()); }
                        return;
                    }

                    // Secure the password
                    String salt = PasswordUtil.generateSalt();
                    String hash = PasswordUtil.hashPassword(password, salt);
                    User newUser = new User(name, phone, email, hash, salt);

                    // Save to database
                    if (UserRepository.registerUser(newUser, conn)) {
                        String response = "Registration successful";
                        exchange.sendResponseHeaders(201, response.length());
                        try (OutputStream os = exchange.getResponseBody()) { os.write(response.getBytes()); }
                    } else {
                        throw new Exception("Database failed to insert user");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                String response = "Internal Server Error";
                exchange.sendResponseHeaders(500, response.length());
                try (OutputStream os = exchange.getResponseBody()) { os.write(response.getBytes()); }
            }
        });

        // 3. Static file handler (catch-all)
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            
            try (InputStream is = MainServer.class.getResourceAsStream("/public" + path)) {
                if (is == null) {
                    String error = "404 Not Found";
                    exchange.sendResponseHeaders(404, error.length());
                    try (OutputStream os = exchange.getResponseBody()) { os.write(error.getBytes()); }
                    return;
                }
                byte[] response = is.readAllBytes(); 
                exchange.sendResponseHeaders(200, response.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(response); }
            }
        });
        
        server.setExecutor(null);
    }

    // Helper method to parse URL-encoded form data in plain Java
    private Map<String, String> parseFormData(String body) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return map;
    }

    public void start() { server.start(); }
    public void stop() { server.stop(0); }

    public static void main(String[] args) throws IOException {
        MainServer server = new MainServer(8080);
        server.start();
        System.out.println("Server started on port 8080");
    }
}