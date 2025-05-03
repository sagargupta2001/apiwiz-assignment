package org.apiwiz.api;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apiwiz.api.annotations.SyncClient;
import org.apiwiz.http.HttpDeleteWithBody;
import org.apiwiz.model.ApiMethod;
import org.apiwiz.model.RequestDTO;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@SyncClient
public class SyncRestFactory implements ApiFactory<HttpResponse> {
    @Override
    public HttpResponse executeRequest(
            ApiMethod apiMethod,
            RequestDTO requestDTO,
            @Nullable SSLConnectionSocketFactory sslConnectionSocketFactory,
            int timeout
    ) throws IOException {
        return switch (apiMethod) {
            case GET -> invokeGet(requestDTO.getUrl(), requestDTO.getHeaderVariables(), sslConnectionSocketFactory, timeout);
            case POST -> invokePost(requestDTO, sslConnectionSocketFactory, timeout);
            case PUT -> invokePut(requestDTO, sslConnectionSocketFactory, timeout);
            case DELETE -> invokeDelete(requestDTO, sslConnectionSocketFactory, timeout);
            case PATCH -> invokePatch(requestDTO, sslConnectionSocketFactory, timeout);
            case OPTIONS -> invokeOptions(requestDTO.getUrl(), requestDTO.getHeaderVariables(), sslConnectionSocketFactory, timeout);
        };
    }

    private HttpResponse invokeGet(String url, Map<String, String> headers,
                                   SSLConnectionSocketFactory sslFactory, int timeout) throws IOException {
        HttpClient client = getClient(sslFactory, timeout);
        HttpGet request = new HttpGet(url);
        addHeaders(request, headers);
        return client.execute(request);
    }

    private HttpResponse invokePost(RequestDTO dto, SSLConnectionSocketFactory sslFactory, int timeout) throws IOException {
        HttpClient client = getClient(sslFactory, timeout);
        HttpPost request = new HttpPost(dto.getUrl());
        prepareEntityRequest(dto, request);
        return client.execute(request);
    }

    private HttpResponse invokePut(RequestDTO dto, SSLConnectionSocketFactory sslFactory, int timeout) throws IOException {
        HttpClient client = getClient(sslFactory, timeout);
        HttpPut request = new HttpPut(dto.getUrl());
        prepareEntityRequest(dto, request);
        return client.execute(request);
    }

    private HttpResponse invokeDelete(RequestDTO dto, SSLConnectionSocketFactory sslFactory, int timeout) throws IOException {
        HttpClient client = getClient(sslFactory, timeout);
        HttpRequestBase request;
        if (dto.getRequestBody() != null && !dto.getRequestBody().isEmpty()) {
            request = new HttpDeleteWithBody(dto.getUrl());
            ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(dto.getRequestBody(), ContentType.APPLICATION_JSON));
        } else {
            request = new HttpDelete(dto.getUrl());
        }
        addHeaders(request, dto.getHeaderVariables());
        return client.execute(request);
    }

    private HttpResponse invokePatch(RequestDTO dto, SSLConnectionSocketFactory sslFactory, int timeout) throws IOException {
        HttpClient client = getClient(sslFactory, timeout);
        HttpPatch request = new HttpPatch(dto.getUrl());
        request.setEntity(new StringEntity(dto.getRequestBody(), ContentType.APPLICATION_JSON));
        addHeaders(request, dto.getHeaderVariables());
        return client.execute(request);
    }

    private HttpResponse invokeOptions(String url, Map<String, String> headers,
                                       SSLConnectionSocketFactory sslFactory, int timeout) throws IOException {
        HttpClient client = getClient(sslFactory, timeout);
        HttpOptions request = new HttpOptions(url);
        addHeaders(request, headers);
        return client.execute(request);
    }

    private void prepareEntityRequest(RequestDTO dto, HttpEntityEnclosingRequestBase request) throws UnsupportedEncodingException {
        String type = dto.getBodyType();
        if (dto.getRequestBody() == null && type != null && type.equals("multi-part")) {
            request.removeHeaders("Content-Type");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            if (dto.getParams() != null) {
                for (Map<String, String> param : dto.getParams()) {
                    param.forEach((k, v) -> builder.addTextBody(k, v, ContentType.DEFAULT_BINARY));
                }
            }
            request.setEntity(builder.build());
        } else if (dto.getRequestBody() == null) {
            List<NameValuePair> formParams = new ArrayList<>();
            if (dto.getParams() != null) {
                for (Map<String, String> param : dto.getParams()) {
                    param.forEach((k, v) -> formParams.add(new BasicNameValuePair(k, v)));
                }
            }
            request.setEntity(new UrlEncodedFormEntity(formParams));
        } else {
            request.setEntity(new StringEntity(dto.getRequestBody(), ContentType.APPLICATION_JSON));
        }
        addHeaders(request, dto.getHeaderVariables());
    }

    private HttpClient getClient(SSLConnectionSocketFactory sslFactory, int timeout) {
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .build();
        return (sslFactory != null)
                ? HttpClientBuilder.create().setSSLSocketFactory(sslFactory).setDefaultRequestConfig(config).build()
                : HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }

    private void addHeaders(HttpRequestBase request, Map<String, String> headers) {
        if (headers != null) {
            headers.forEach(request::addHeader);
        }
    }


}
