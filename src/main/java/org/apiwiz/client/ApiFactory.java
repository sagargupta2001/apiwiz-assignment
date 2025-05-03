package org.apiwiz.client;

import jakarta.annotation.Nullable;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apiwiz.model.ApiMethod;
import org.apiwiz.model.RequestDTO;

public interface ApiFactory<T> {
    T executeRequest(
            ApiMethod apiMethod,
            RequestDTO requestDTO,
            @Nullable SSLConnectionSocketFactory sslConnectionSocketFactory,
            int timeout
    ) throws Exception;
}
