package com.myapp;


import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class MainServer {

    private final HttpServer server;

    public MainServer(int port) throws IOException{
       
        server = HttpServer.create(new InetSocketAddress(port),0); 

         // 1. Health endpoint (specific paths are matched first)
        server.createContext("/api/health", exchange -> {
            String response = "OK";
            exchange.sendResponseHeaders(200,response.length());
            try (OutputStream os = exchange.getResponseBody() ){
                os.write(response.getBytes());
            }
        });

          // 2. Static file handler (catch-all for the frontend)
          server.createContext("/",exchange -> {
            String path = exchange.getRequestURI().getPath();

            // If the user just visits "localhost:8080/", default to index.html
            if (path.equals("/")) {
                path = "/index.html"; 
            }

             // Look for the file in src/main/resources/public
             try (InputStream is = MainServer.class.getResourceAsStream("/public" + path)) {
                if (is == null) {
                    String error = "404 Not Found";
                    exchange.sendResponseHeaders(404, error.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(error.getBytes());
                    }
                    return;
                }
                
                // Read the file and send it
                byte[] response = is.readAllBytes(); 
                exchange.sendResponseHeaders(200, response.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            }
        });
        
        
        server.setExecutor(null);
    }

    public void start(){
        server.start();
    }

    public void stop() {
        // Stop the server (0 means don't wait for active connections to finish)
        server.stop(0);
    }

    public static void main(String[] args) throws IOException{
       MainServer server = new MainServer(8080);
       server.start();
       System.out.println("Server started on port 8080");
    }
}