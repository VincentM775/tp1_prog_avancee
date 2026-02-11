package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.validation;

import java.util.regex.Pattern;

public class FormValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[A-Za-z0-9_]{3,50}$"
    );

    public static void validateRequired(ValidationResult result, String field, String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            result.addError(field, message);
        }
    }

    public static void validateMinLength(ValidationResult result, String field, String value, int minLength, String message) {
        if (value != null && value.trim().length() < minLength) {
            result.addError(field, message);
        }
    }

    public static void validateMaxLength(ValidationResult result, String field, String value, int maxLength, String message) {
        if (value != null && value.trim().length() > maxLength) {
            result.addError(field, message);
        }
    }

    public static void validateEmail(ValidationResult result, String field, String value, String message) {
        if (value != null && !value.trim().isEmpty() && !EMAIL_PATTERN.matcher(value.trim()).matches()) {
            result.addError(field, message);
        }
    }

    public static void validateUsername(ValidationResult result, String field, String value, String message) {
        if (value != null && !USERNAME_PATTERN.matcher(value.trim()).matches()) {
            result.addError(field, message);
        }
    }

    public static void validateEquals(ValidationResult result, String field, String value1, String value2, String message) {
        if (value1 == null || !value1.equals(value2)) {
            result.addError(field, message);
        }
    }

    public static void validateRequiredWithLength(ValidationResult result, String field, String value,
                                                   int minLength, int maxLength,
                                                   String requiredMsg, String lengthMsg) {
        if (value == null || value.trim().isEmpty()) {
            result.addError(field, requiredMsg);
        } else if (value.trim().length() < minLength || value.trim().length() > maxLength) {
            result.addError(field, lengthMsg);
        }
    }
}
