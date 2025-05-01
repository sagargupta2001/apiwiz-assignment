package org.apiwiz.api;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.core.buffer.Buffer;
import org.apiwiz.model.ApiMethod;
import org.apiwiz.model.RequestDTO;

import java.io.IOException;

public interface ApiFactory {
    public Uni<HttpResponse<Buffer>> executeTarget(ApiMethod apiMethod,
                                                   RequestDTO requestDTO,
                                                   int timeout) throws IOException;
}
