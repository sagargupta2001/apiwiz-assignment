package org.apiwiz.api;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apiwiz.model.RequestDTOWrapper;

import java.net.URI;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApiResource {

    @Inject
    RestFactory restFactory;

    @POST
    @Path("/invoke")
    public Uni<Response> invokeApi(RequestDTOWrapper requestDTO) {
        try {
            URI uri = URI.create(requestDTO.getRequestDTO().getUrl());
            // Validate URL, etc.

            // Call the RestFactory to execute the request
            return restFactory.executeRequest(requestDTO.getApiMethod(), requestDTO)
                    .onItem().transform(response -> Response.status(response.statusCode())
                            .entity(response.bodyAsString()).build())
                    .onFailure().recoverWithItem(ex -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Request failed: " + ex.getMessage()).build());
        } catch (Exception e) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid URL: " + e.getMessage()).build());
        }
    }
}