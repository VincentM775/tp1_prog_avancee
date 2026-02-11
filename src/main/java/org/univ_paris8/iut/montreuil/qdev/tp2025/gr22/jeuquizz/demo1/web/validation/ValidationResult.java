package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.validation;

import java.util.HashMap;
import java.util.Map;

public class ValidationResult {

    private final Map<String, String> errors = new HashMap<>();

    public void addError(String field, String message) {
        errors.put(field, message);
    }

    public boolean hasError(String field) {
        return errors.containsKey(field);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public String getError(String field) {
        return errors.get(field);
    }

    public int getErrorCount() {
        return errors.size();
    }
}
