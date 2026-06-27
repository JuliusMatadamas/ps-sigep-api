package com.sigep.utils;

import java.util.Locale;

import com.sigep.constants.AppConstants;

public class ValidationUtil {

    private ValidationUtil() {
        throw new IllegalStateException("Clase de utilidad, no debe ser instanciada.");
    }

    public static boolean isValidString(String value) {
        return isValidString(value, 0, Integer.MAX_VALUE);
    }

    public static boolean isValidString(String value, int minLength) {
        return isValidString(value, minLength, Integer.MAX_VALUE);
    }

    public static boolean isValidString(String value, int minLength, int maxLength) {
        if (value == null) {
            return false;
        }

        // Rechazar caracteres de control peligrosos
        for (String controlChar : AppConstants.getControlChars()) {
            if (value.contains(controlChar)) {
                return false;
            }
        }

        // Validar contra inyeccion SQL
        String lowerValue = value.toLowerCase(Locale.ROOT);
        for (String pattern : AppConstants.getSqlInjectionPatterns()) {
            if (lowerValue.contains(pattern.toLowerCase())) {
                return false;
            }
        }

        // Validar contra XSS
        for (String pattern : AppConstants.getXssPatterns()) {
            if (lowerValue.contains(pattern.toLowerCase())) {
                return false;
            }
        }

        // Validar longitud despues de trim
        String trimmed = value.trim();
        int length = trimmed.length();

        return length >= minLength && length <= maxLength;
    }
}
