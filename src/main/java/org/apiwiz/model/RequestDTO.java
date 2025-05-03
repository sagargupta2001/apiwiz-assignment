package org.apiwiz.model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDTO {

    private String url;
    private Map<String, String> headerVariables;
    private String bodyType;
    private String requestBody;
    private List<Map<String, String>> params;

    /**
     * Utility method to add query parameters to the URL.
     */
    public String buildUrlWithParams() {
        if (params == null || params.isEmpty()) {
            return url;
        }

        StringBuilder urlWithParams = new StringBuilder(url);
        urlWithParams.append("?");

        params.forEach(param -> {
            param.forEach((key, value) -> {
                urlWithParams.append(key)
                        .append("=")
                        .append(value)
                        .append("&");
            });
        });

        // Remove last "&"
        urlWithParams.deleteCharAt(urlWithParams.length() - 1);

        return urlWithParams.toString();
    }
}
