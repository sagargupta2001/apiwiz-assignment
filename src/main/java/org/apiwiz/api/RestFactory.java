package org.apiwiz.api;

import io.smallrye.mutiny.TimeoutException;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apiwiz.model.ApiMethod;
import org.apiwiz.model.RequestDTO;

import java.util.Map;

@ApplicationScoped
public class RestFactory implements ApiFactory {

    @Inject
    Vertx vertx;

    @Override
    public Uni<HttpResponse<Buffer>> executeTarget(ApiMethod apiMethod, RequestDTO requestDTO, int timeout) {
        WebClient client = WebClient.create(vertx);

        return switch (apiMethod) {
            case GET -> invokeGet(client, requestDTO, timeout);
            case POST -> invokePost(client, requestDTO, timeout);
            // Implement other methods...
            default -> throw new UnsupportedOperationException("Unsupported method: " + apiMethod);
        };
    }

    private Uni<HttpResponse<Buffer>> invokeGet(WebClient client, RequestDTO requestDTO, int timeout) {
        HttpRequest<Buffer> request = client.getAbs(requestDTO.getUrl());
        addHeaders(request, requestDTO.getHeaderVariables());

        // Set the timeout for the request (in milliseconds)
        request.timeout(timeout);

        return request.send()
                .onItem().transform(response -> response) // Return the response if successful
                .onFailure().recoverWithUni(throwable -> {
                    // Handle TimeoutException
                    if (throwable instanceof java.util.concurrent.TimeoutException) {
                        // Handle timeout specifically, log or return custom response
                        return Uni.createFrom().failure(new TimeoutException());
                    }

                    // Handle other exceptions (e.g., connection failure, invalid URL)
                    return Uni.createFrom().failure(new RuntimeException("Request failed: " + throwable.getMessage(), throwable));
                });
    }

    private Uni<HttpResponse<Buffer>> invokePost(WebClient client, RequestDTO requestDTO, int timeout) {
        HttpRequest request = client.postAbs(requestDTO.getUrl());
        addHeaders(request, requestDTO.getHeaderVariables());
        if (requestDTO.getRequestBody() != null) {
            request.sendJson(requestDTO.getRequestBody());
        }

        request.timeout(timeout);

        return request.send().onItem().transform(response -> response);
    }

    private void addHeaders(HttpRequest request, Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.putHeader(entry.getKey(), entry.getValue());
            }
        }
    }
}
