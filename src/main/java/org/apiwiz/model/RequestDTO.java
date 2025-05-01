package org.apiwiz.model;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDTO {
    private ApiMethod apiMethod;
    private String url;
    private Map<String, String> headerVariables;
    private String bodyType;
    private String requestBody;
    private List<Map<String, String>> params;
}
