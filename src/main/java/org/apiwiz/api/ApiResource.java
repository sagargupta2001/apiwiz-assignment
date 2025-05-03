package org.apiwiz.api;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apiwiz.model.RequestDTOWrapper;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApiResource {

    @Inject
    ApiFactory apiFactory;

    @POST
    @Path("/invoke")
    public Uni<Response> invokeApi(RequestDTOWrapper requestDTOWrapper) {
        long startTime = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();
        System.out.println("START request on thread: " + threadName + " at " + startTime + " for URL: " + requestDTOWrapper.getRequestDTO().getUrl());

        try {
            return apiFactory.executeRequest(requestDTOWrapper.getApiMethod(), requestDTOWrapper.getRequestDTO(), requestDTOWrapper.getTimeout())
                    .onItem().transform(response -> {
                        long endTime = System.currentTimeMillis();
                        System.out.println("END request on thread: " + threadName + " at " + endTime + " (duration: " + (endTime - startTime) + "ms)");
                        return Response.status(response.statusCode()).entity(response.bodyAsString()).build();
                    })
                    .onFailure().recoverWithItem(ex -> {
                        long endTime = System.currentTimeMillis();
                        System.out.println("FAIL request on thread: " + threadName + " at " + endTime + " (duration: " + (endTime - startTime) + "ms)");
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                .entity("Request failed: " + ex.getMessage()).build();
                    });

        } catch (Exception e) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid URL: " + e.getMessage()).build());
        }
    }

}