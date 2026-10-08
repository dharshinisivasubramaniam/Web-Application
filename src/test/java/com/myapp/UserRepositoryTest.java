package com.myapp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserRepositoryTest {
    private Connection conn;

    @BeforeEach
    void setUp() throws SQLException {
        // Connect to MySQL
        conn = DatabaseManager.getConnection();
        // Turn off auto-commit so we can rollback later!
        conn.setAutoCommit(false); 
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (conn != null) {
            // Undo all test inserts!
            conn.rollback(); 
            conn.close();
        }
    }

    @Test
    void testRegisterUserAndCheckEmail() throws SQLException {
        User testUser = new User("John Doe", "1234567890", "john@test.com", "hash", "salt");
        
        // 1. Ensure email doesn't exist yet
        assertFalse(UserRepository.emailExists(testUser.email, conn), "Email should not exist initially");
        
        // 2. Register user
        boolean success = UserRepository.registerUser(testUser, conn);
        assertTrue(success, "Registration should succeed");
        
        // 3. Ensure email exists now
        assertTrue(UserRepository.emailExists(testUser.email, conn), "Email should exist after registration");
    }
}