package org.apiwiz.api;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apiwiz.model.ApiMethod;
import org.apiwiz.model.RequestDTOWrapper;

import java.util.Map;

@ApplicationScoped
public class RestFactory {

    @Inject
    Vertx vertx;

    public Uni<HttpResponse<Buffer>> executeRequest(ApiMethod apiMethod, RequestDTOWrapper requestDTO) {
        WebClient client = WebClient.create(vertx);
        HttpRequest<Buffer> request = createRequest(client, apiMethod, requestDTO);

        if (requestDTO.getTimeout() > 0) {
            request.timeout(requestDTO.getTimeout());
        }

        return request.send()
                .onItem().transform(response -> response)
                .onFailure().recoverWithUni(throwable -> {
                    // Handle failure or timeout here
                    return Uni.createFrom().failure(new RuntimeException("Request failed: " + throwable.getMessage()));
                });
    }

    private HttpRequest<Buffer> createRequest(WebClient client, ApiMethod apiMethod, RequestDTOWrapper requestDTO) {
        String url = requestDTO.getRequestDTO().getUrl();
        HttpRequest<Buffer> request = switch (apiMethod) {
            case GET -> client.getAbs(url);
            case POST -> client.postAbs(url);
            case PUT -> client.putAbs(url);
            case DELETE -> client.deleteAbs(url);
            default -> throw new UnsupportedOperationException("Unsupported HTTP method: " + apiMethod);
        };
        addHeaders(request, requestDTO.getRequestDTO().getHeaderVariables());
        return request;
    }

    private void addHeaders(HttpRequest<Buffer> request, Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.putHeader(entry.getKey(), entry.getValue());
            }
        }
    }
}
