package org.example.webmessenger.java.model;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    public static String hashUserFields(String... fields) {
        try {
            StringBuilder input = new StringBuilder();
            for (String field : fields) {
                if (field != null) {
                    input.append(field).append("|"); // разделитель для устойчивости
                }
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.toString().getBytes(StandardCharsets.UTF_8));

            // Преобразуем в hex-строку
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString(); // 64 символа
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
