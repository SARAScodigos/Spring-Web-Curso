package com.PC.Store.SistemaWeb.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordHashUtil {

    private PasswordHashUtil() {}

    public static String encode(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : encoded) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No se pudo generar el hash de la contraseña", e);
        }
    }

    public static boolean matches(String rawPassword, String storedHash) {
        return storedHash != null && encode(rawPassword).equals(storedHash);
    }
}
