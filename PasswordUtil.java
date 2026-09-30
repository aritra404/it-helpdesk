import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * PasswordUtil provides secure password hashing and verification using
 * standard Java cryptography (PBKDF2 with HMAC-SHA256).
 */
public class PasswordUtil {

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 128; // bits
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * Generates a cryptographically strong random salt (16 bytes) encoded in Base64.
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes a plain-text password with a given salt using PBKDF2WithHmacSHA256.
     */
    public static String hashPassword(String password, String saltBase64) {
        try {
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password: " + e.getMessage(), e);
        }
    }

    /**
     * Verifies if a raw password matches the stored hash and salt.
     */
    public static boolean verifyPassword(String rawPassword, String storedHash, String storedSalt) {
        String computedHash = hashPassword(rawPassword, storedSalt);
        return computedHash.equals(storedHash);
    }

    // Helper main to print test hashes for initial SQL seeding
    public static void main(String[] args) {
        String[] users = {"admin", "alice", "bob", "charlie", "david", "eva"};
        String[] passwords = {"admin123", "alice123", "bob123", "charlie123", "david123", "eva123"};

        for (int i = 0; i < users.length; i++) {
            String salt = generateSalt();
            String hash = hashPassword(passwords[i], salt);
            System.out.println(users[i] + " (" + passwords[i] + "):");
            System.out.println("  SALT: " + salt);
            System.out.println("  HASH: " + hash);
        }
    }
}
