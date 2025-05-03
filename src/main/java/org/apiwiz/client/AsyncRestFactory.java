package org.apiwiz.client;

import io.smallrye.mutiny.TimeoutException;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpRequest;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.core.Vertx;
import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apiwiz.annotations.AsyncClient;
import org.apiwiz.error.RequestTimeoutException;
import org.apiwiz.model.ApiMethod;
import org.apiwiz.model.RequestDTO;

import java.util.Map;

@ApplicationScoped
@AsyncClient
public class AsyncRestFactory implements ApiFactory<Uni<HttpResponse<Buffer>>> {

    @Inject
    Vertx vertx;

    @Override
    public Uni<HttpResponse<Buffer>> executeRequest(
            ApiMethod apiMethod,
            RequestDTO requestDTO,
            @Nullable SSLConnectionSocketFactory sslConnectionSocketFactory,
            int timeout
    ) {
        WebClient client = WebClient.create(vertx);
        HttpRequest<Buffer> request = createRequest(client, apiMethod, requestDTO);

        if (timeout > 0)
            request.timeout(timeout);

        return request.send()
                .onItem().transform(response -> response)
                .onFailure()
                .recoverWithUni(throwable -> {
                    if (isTimeout(throwable)) {
                        return Uni.createFrom().failure(
                                new RequestTimeoutException("Request timed out", throwable)
                        );
                    }
                    return Uni.createFrom().failure(
                            new RuntimeException("Request failed: " + throwable.getMessage(), throwable)
                    );
                });
    }

    private HttpRequest<Buffer> createRequest(WebClient client, ApiMethod apiMethod, RequestDTO requestDTO) {
        String url = requestDTO.getUrl();
        HttpRequest<Buffer> request = switch (apiMethod) {
            case GET -> client.getAbs(url);
            case POST -> client.postAbs(url);
            case PUT -> client.putAbs(url);
            case DELETE -> client.deleteAbs(url);
            case PATCH, OPTIONS -> client.requestAbs(HttpMethod.valueOf(apiMethod.name()), url);
        };
        addHeaders(request, requestDTO.getHeaderVariables());
        return request;
    }

    private void addHeaders(HttpRequest<Buffer> request, Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.putHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    private boolean isTimeout(Throwable throwable) {
        return throwable instanceof TimeoutException ||
                throwable.getCause() instanceof TimeoutException ||
                throwable.getMessage().toLowerCase().contains("timeout");
    }

}
