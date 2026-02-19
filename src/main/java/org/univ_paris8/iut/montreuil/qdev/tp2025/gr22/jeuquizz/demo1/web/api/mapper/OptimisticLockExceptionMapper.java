package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.mapper;

import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.dto.ApiError;

/**
 * Intercepte les OptimisticLockException (conflit de version @Version)
 * et retourne HTTP 409 Conflict.
 * Cela se produit quand deux utilisateurs modifient la même annonce en même temps.
 */
@Provider
public class OptimisticLockExceptionMapper implements ExceptionMapper<OptimisticLockException> {

    @Override
    public Response toResponse(OptimisticLockException e) {
        ApiError error = new ApiError(
                409,
                "Conflict",
                "La ressource a été modifiée par un autre utilisateur. Veuillez recharger et réessayer."
        );

        return Response.status(Response.Status.CONFLICT)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
