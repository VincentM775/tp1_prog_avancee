package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.exception;

import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.dto.ApiError;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound() {
        ResponseEntity<ApiError> resp = handler.handleNotFound(new EntityNotFoundException("Annonce", 1L));
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertEquals(404, resp.getBody().getStatus());
    }

    @Test
    void handleBusiness() {
        ResponseEntity<ApiError> resp = handler.handleBusiness(new BusinessException("erreur"));
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void handleConflict() {
        ResponseEntity<ApiError> resp = handler.handleConflict(new ConflictException("conflit"));
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    void handleForbidden() {
        ResponseEntity<ApiError> resp = handler.handleForbidden(new ForbiddenException("interdit"));
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    @Test
    void handleAccessDenied() {
        ResponseEntity<ApiError> resp = handler.handleAccessDenied(new AccessDeniedException("denied"));
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    @Test
    void handleOptimisticLock() {
        ResponseEntity<ApiError> resp = handler.handleOptimisticLock(new OptimisticLockException("lock"));
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    void handleGeneric() {
        ResponseEntity<ApiError> resp = handler.handleGeneric(new RuntimeException("oops"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertEquals(500, resp.getBody().getStatus());
    }
}
