package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    // ==================== ApiError ====================

    @Test
    void apiError_constructeur3args() {
        ApiError err = new ApiError(404, "Not Found", "Non trouvé");
        assertEquals(404, err.getStatus());
        assertEquals("Not Found", err.getError());
        assertEquals("Non trouvé", err.getMessage());
        assertNotNull(err.getTimestamp());
        assertNull(err.getDetails());
    }

    @Test
    void apiError_constructeur4args() {
        var detail = new ApiError.FieldError("field", "msg");
        ApiError err = new ApiError(400, "Bad Request", "Validation", List.of(detail));
        assertEquals(400, err.getStatus());
        assertNotNull(err.getDetails());
        assertEquals(1, err.getDetails().size());
    }

    @Test
    void apiError_setters() {
        ApiError err = new ApiError();
        err.setStatus(500);
        err.setError("Error");
        err.setMessage("Message");
        err.setTimestamp("2025-01-01");
        err.setDetails(List.of());

        assertEquals(500, err.getStatus());
        assertEquals("Error", err.getError());
        assertEquals("Message", err.getMessage());
        assertEquals("2025-01-01", err.getTimestamp());
        assertNotNull(err.getDetails());
    }

    // ==================== ApiError.FieldError ====================

    @Test
    void fieldError_constructeurEtGetters() {
        ApiError.FieldError fe = new ApiError.FieldError("name", "required");
        assertEquals("name", fe.getField());
        assertEquals("required", fe.getMessage());
    }

    @Test
    void fieldError_setters() {
        ApiError.FieldError fe = new ApiError.FieldError();
        fe.setField("email");
        fe.setMessage("invalid");
        assertEquals("email", fe.getField());
        assertEquals("invalid", fe.getMessage());
    }

    // ==================== LoginResponse ====================

    @Test
    void loginResponse_constructeurEtGetter() {
        LoginResponse lr = new LoginResponse("tokenXYZ");
        assertEquals("tokenXYZ", lr.getToken());
    }

    @Test
    void loginResponse_setter() {
        LoginResponse lr = new LoginResponse();
        lr.setToken("abc");
        assertEquals("abc", lr.getToken());
    }

    // ==================== UpdateAnnonceDTO ====================

    @Test
    void updateAnnonceDTO_setters() {
        UpdateAnnonceDTO dto = new UpdateAnnonceDTO();
        dto.setTitle("T");
        dto.setDescription("D");
        dto.setAdress("A");
        dto.setMail("m@m.com");
        dto.setCategoryId(5L);

        assertEquals("T", dto.getTitle());
        assertEquals("D", dto.getDescription());
        assertEquals("A", dto.getAdress());
        assertEquals("m@m.com", dto.getMail());
        assertEquals(5L, dto.getCategoryId());
    }
}
