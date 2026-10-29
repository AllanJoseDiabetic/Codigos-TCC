package app.commerce.security;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Component;

@Component
public class PasswordHasher {

    private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private final SecureRandom secureRandom = new SecureRandom();

    public String hash(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                "A senha nao pode estar vazia."
            );
        }

        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        PBEKeySpec specification = new PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH
        );

        try {
            SecretKeyFactory factory =
                SecretKeyFactory.getInstance(
                    "PBKDF2WithHmacSHA256"
                );

            byte[] hash = factory
                .generateSecret(specification)
                .getEncoded();

            Base64.Encoder encoder = Base64.getEncoder();

            return "pbkdf2-sha256"
                + "$"
                + ITERATIONS
                + "$"
                + encoder.encodeToString(salt)
                + "$"
                + encoder.encodeToString(hash);

        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException(
                "Nao foi possivel processar a senha.",
                exception
            );

        } finally {
            specification.clearPassword();
        }
    }
}