/**
 * CineMax - Laboratorio Interdisciplinare A
 *
 * Autori:
 * - Trupia Giovanni 766370 Como
 * - Mahhay Harman 762686 Como
 * - Ait Laarabi Morad 762740 Como
 * - Maatouch Ayman 766465 Como
 *
 * JDK 21
 */
package cinemax.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

// Classe che gestisce le password
public class Password {

    // Hash con salt -> restituisce "salt:hash" (Base64)
    public static String hashPassword(String password) throws Exception {
        if (password.isBlank()) throw new IllegalArgumentException("La password non puo' essere vuota");

        byte[] salt = generateSalt();

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

        String saltB64 = Base64.getEncoder().encodeToString(salt);
        String hashB64 = Base64.getEncoder().encodeToString(hash);
        return saltB64 + ":" + hashB64;
    }


    public static boolean checkPassword(String inputPassword, String storedHash) throws Exception {
        if (inputPassword.isBlank()) throw new IllegalArgumentException("La password non puo' essere vuota");

        String[] parts = storedHash.split(":");
        if (parts.length != 2) throw new IllegalArgumentException("Hash non valido");

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[1]);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] actualHash = md.digest(inputPassword.getBytes(StandardCharsets.UTF_8));

        return MessageDigest.isEqual(actualHash, expectedHash); // Timing-safe
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}
