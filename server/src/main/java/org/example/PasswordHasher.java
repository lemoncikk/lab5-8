package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.AppException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
@Slf4j
public class PasswordHasher {
    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD2");
            byte[] hashBytes = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Critical error while password hashing", e);
            throw new AppException("Critical error while password hashing");
        }
    }
}
