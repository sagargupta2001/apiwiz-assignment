package org.apiwiz.api;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import org.apiwiz.model.ApiMethod;
import org.apiwiz.model.RequestDTO;

public class SyncRestFactory implements ApiFactory{

    @Override
    public Uni<HttpResponse<Buffer>> executeRequest(ApiMethod apiMethod, RequestDTO requestDTO, int timeout) {
        return null;
    }
}
