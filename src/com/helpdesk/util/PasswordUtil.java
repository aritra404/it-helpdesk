package com.helpdesk.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * PasswordUtil provides secure password hashing and verification
 * using PBKDF2WithHmacSHA256 with randomly generated salt.
 */
public class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 128; // 128-bit key length
    private static final int SALT_LENGTH = 16;  // 16 bytes salt

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generates a cryptographically strong 16-byte random salt, Base64-encoded.
     * @return Base64 encoded salt string
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes a raw plaintext password with a given salt using PBKDF2WithHmacSHA256.
     * @param rawPassword Plain-text password entered by the user
     * @param base64Salt Base64-encoded salt
     * @return Base64 encoded hash string
     */
    public static String hashPassword(String rawPassword, String base64Salt) {
        try {
            byte[] salt = Base64.getDecoder().decode(base64Salt);
            KeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while hashing password with PBKDF2: " + e.getMessage(), e);
        }
    }

    /**
     * Verifies if a raw password matches the stored hash when hashed with the stored salt.
     * @param rawPassword Plain-text password entered by the user
     * @param storedHash Base64-encoded hash from the database
     * @param storedSalt Base64-encoded salt from the database
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String rawPassword, String storedHash, String storedSalt) {
        if (rawPassword == null || storedHash == null || storedSalt == null) {
            return false;
        }
        String computedHash = hashPassword(rawPassword, storedSalt);
        return computedHash.equals(storedHash);
    }
}
