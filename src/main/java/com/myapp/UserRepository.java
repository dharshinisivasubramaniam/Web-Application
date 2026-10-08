package com.myapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    
    // Checks if the email is already in the database
    public static boolean emailExists(String email, Connection conn) throws SQLException {
        String sql = "SELECT id FROM users WHERE email = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                // If rs.next() is true, it means it found at least one row!
                return rs.next(); 
            }
        }
    }

    // Securely inserts the new user into the database
    public static boolean registerUser(User user, Connection conn) throws SQLException {
        String sql = "INSERT INTO users (name, phone, email, password_hash, password_salt) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.name);
            stmt.setString(2, user.phone);
            stmt.setString(3, user.email);
            stmt.setString(4, user.passwordHash);
            stmt.setString(5, user.passwordSalt);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Returns true if the row was successfully inserted
        }
    }
}