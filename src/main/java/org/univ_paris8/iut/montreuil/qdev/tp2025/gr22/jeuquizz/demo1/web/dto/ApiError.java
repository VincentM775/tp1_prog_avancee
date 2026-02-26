package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.dto;

import java.time.LocalDateTime;
import java.util.List;

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
