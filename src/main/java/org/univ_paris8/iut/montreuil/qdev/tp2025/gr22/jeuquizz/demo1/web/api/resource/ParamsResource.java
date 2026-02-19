package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/params")
public class ParamsResource {

    /**
     * Exemple avec QueryParams : GET /api/params?nom=Jean&age=25
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response withQueryParams(
            @QueryParam("nom") String nom,
            @QueryParam("age") Integer age) {

        return Response.ok(Map.of(
                "type", "QueryParams",
                "nom", nom != null ? nom : "non renseigné",
                "age", age != null ? age : "non renseigné"
        )).build();
    }

    /**
     * Exemple avec PathParams : GET /api/params/Jean/25
     */
    @GET
    @Path("/{nom}/{age}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response withPathParams(
            @PathParam("nom") String nom,
            @PathParam("age") int age) {

        return Response.ok(Map.of(
                "type", "PathParams",
                "nom", nom,
                "age", age
        )).build();
    }
}
