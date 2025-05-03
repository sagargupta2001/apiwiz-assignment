package org.apiwiz.model;

public enum ApiMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS");

    private final String method;

    ApiMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
