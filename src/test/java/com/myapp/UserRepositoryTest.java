package com.myapp;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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


    // LOGIN CODE
    @Test 
    void testGetUserByEmail() throws SQLException {
        User testUser = new User("Jane Doe","0987654321","jane@test.com", "storedHash", "storedSalt");
        UserRepository.registerUser(testUser, conn);

           // 1. Fetch the user we just registered
        User retrievedUser = UserRepository.getUserByEmail("jane@test.com",conn);

          // 2. Verify the data matches
          org.junit.jupiter.api.Assertions.assertNotNull(retrievedUser, "User should be found");
          org.junit.jupiter.api.Assertions.assertEquals("storedHash",retrievedUser.passwordHash);

           // 3. Verify an unknown email returns null
           User missingUser = UserRepository.getUserByEmail("nobody@test.com",conn);
           org.junit.jupiter.api.Assertions.assertNull(missingUser, "Unknown email should return null");
    }
}