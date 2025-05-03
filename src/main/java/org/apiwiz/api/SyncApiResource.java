package org.apiwiz.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apiwiz.annotations.SyncClient;
import org.apiwiz.client.ApiFactory;
import org.apiwiz.model.RequestDTOWrapper;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

@Path("/api/sync")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SyncApiResource {

    @Inject
    @SyncClient
    ApiFactory<HttpResponse> syncApiFactory;

    @POST
    @Path("/invoke")
    public Response invoke(RequestDTOWrapper requestDTOWrapper) {
        long startTime = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();

        System.out.printf("START SYNC on thread: %s at %d for URL: %s%n",
                threadName, startTime, requestDTOWrapper.getRequestDTO().getUrl());

        try {
            HttpResponse httpResponse = syncApiFactory.executeRequest(
                    requestDTOWrapper.getApiMethod(),
                    requestDTOWrapper.getRequestDTO(),
                    null,
                    requestDTOWrapper.getTimeout()
            );

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            String responseBody = httpResponse.getEntity() != null
                    ? EntityUtils.toString(httpResponse.getEntity())
                    : "";

            long endTime = System.currentTimeMillis();
            System.out.printf("END SYNC on thread: %s at %d (duration: %dms)%n",
                    threadName, endTime, endTime - startTime);

            return Response.status(statusCode)
                    .entity(responseBody)
                    .build();

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            System.out.printf("FAIL SYNC on thread: %s at %d (duration: %dms)%n",
                    threadName, endTime, endTime - startTime);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Request failed: " + e.getMessage())
                    .build();
        }
    }
}
