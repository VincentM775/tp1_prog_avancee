package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.mapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.dto.ApiError;

import java.util.List;

/**
 * Intercepte les erreurs de Bean Validation (ConstraintViolationException)
 * et retourne un 400 avec le détail de chaque champ invalide.
 */
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        List<ApiError.FieldError> fieldErrors = e.getConstraintViolations().stream()
                .map(this::toFieldError)
                .toList();

        ApiError error = new ApiError(
                400,
                "Bad Request",
                "Validation échouée",
                fieldErrors
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private ApiError.FieldError toFieldError(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        // Extraire le nom du champ (dernier segment du path)
        String field = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
        return new ApiError.FieldError(field, violation.getMessage());
    }
}
