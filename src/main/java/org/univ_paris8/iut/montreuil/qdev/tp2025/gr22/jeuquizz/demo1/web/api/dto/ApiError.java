package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Format normalisé pour toutes les réponses d'erreur de l'API.
 *
 * Exemple :
 * {
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "Validation échouée",
 *   "timestamp": "2025-01-15T14:30:00",
 *   "details": [
 *     { "field": "title", "message": "Le titre est obligatoire" },
 *     { "field": "mail", "message": "L'email doit être valide" }
 *   ]
 * }
 */
public class ApiError {

    private int status;
    private String error;
    private String message;
    private String timestamp;
    private List<FieldError> details;

    public ApiError() {
    }

    public ApiError(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();
    }

    public ApiError(int status, String error, String message, List<FieldError> details) {
        this(status, error, message);
        this.details = details;
    }

    // --- Getters & Setters ---

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public List<FieldError> getDetails() { return details; }
    public void setDetails(List<FieldError> details) { this.details = details; }

    /**
     * Détail d'une erreur de validation sur un champ.
     */
    public static class FieldError {
        private String field;
        private String message;

        public FieldError() {
        }

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() { return field; }
        public void setField(String field) { this.field = field; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
