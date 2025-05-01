package org.apiwiz.api;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.buffer.Buffer;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apiwiz.model.RequestDTO;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;

import java.net.URI;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApiResource {

    private final WebClient webClient;

    @Inject
    public ApiResource(WebClient webClient) {
        this.webClient = webClient;
    }

    @POST
    @Path("/invoke")
    public Uni<Response> invokeApi(RequestDTO requestDTO) {
        URI uri;
        try {
            uri = URI.create(requestDTO.getUrl());
        } catch (Exception e) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid URL: " + e.getMessage()).build());
        }

        String scheme = uri.getScheme();
        String host = uri.getHost();
        int port = uri.getPort() != -1 ? uri.getPort() : ("https".equalsIgnoreCase(scheme) ? 443 : 80);
        String path = uri.getRawPath() + (uri.getRawQuery() != null ? "?" + uri.getRawQuery() : "");

        Uni<HttpResponse<Buffer>> responseUni;

        switch (requestDTO.getApiMethod()) {
            case GET -> responseUni = webClient.get(port, host, path)
                    .putHeader("Content-Type", requestDTO.getHeaderVariables().getOrDefault("Content-Type", "application/json"))
                    .send();
            case POST -> responseUni = webClient.post(port, host, path)
                    .putHeader("Content-Type", requestDTO.getHeaderVariables().getOrDefault("Content-Type", "application/json"))
                    .sendJson(requestDTO.getRequestBody());
            case PUT -> responseUni = webClient.put(port, host, path)
                    .putHeader("Content-Type", requestDTO.getHeaderVariables().getOrDefault("Content-Type", "application/json"))
                    .sendJson(requestDTO.getRequestBody());
            case DELETE -> responseUni = webClient.delete(port, host, path)
                    .putHeader("Content-Type", requestDTO.getHeaderVariables().getOrDefault("Content-Type", "application/json"))
                    .send();
            default -> {
                return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST)
                        .entity("Unsupported HTTP method").build());
            }
        }

        return responseUni
                .onItem().transform(response -> Response.status(response.statusCode())
                        .entity(response.bodyAsString()).build())
                .onFailure().recoverWithItem(ex -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Request failed: " + ex.getMessage()).build());
    }
}
