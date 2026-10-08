package com.myapp;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

    // 65,536 iterations is the modern standard for PBKDF2
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    // Generates a random cryptographic salt
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        // We convert the raw bytes to a Base64 string so we can save it as text in MySQL
        return Base64.getEncoder().encodeToString(salt);
    }

    // Hashes the plain-text password using the provided salt
    public static String hashPassword(String password, String salt) {
        try {
            // Convert the stored Base64 text back into raw bytes
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            
            // Configure the hashing algorithm parameters
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            
            // Perform the heavy mathematical hashing calculation
            byte[] hash = factory.generateSecret(spec).getEncoded();
            
            // Return the final hash as a Base64 string
            return Base64.getEncoder().encodeToString(hash);
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}