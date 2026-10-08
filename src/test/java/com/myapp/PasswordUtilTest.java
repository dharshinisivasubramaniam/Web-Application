package com.myapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

public class PasswordUtilTest {
     @Test
    void testSaltGenerationIsUnique() {
        String salt1 = PasswordUtil.generateSalt();
        String salt2 = PasswordUtil.generateSalt();
        
        assertNotNull(salt1);
        assertNotEquals(salt1, salt2, "Two generated salts should be unique");
    }

        @Test
    void testPasswordHashingIsConsistent() {
        // We must use a valid Base64 string here for the test!
        String salt = "bW9ja1NhbHQxMjM0NTY3OA==";
        
        String hash1 = PasswordUtil.hashPassword("mySecretPassword", salt);
        String hash2 = PasswordUtil.hashPassword("mySecretPassword", salt);
        
        assertNotNull(hash1);
        assertEquals(hash1, hash2, "Hashing the same password with the same salt should match");
    }
}
