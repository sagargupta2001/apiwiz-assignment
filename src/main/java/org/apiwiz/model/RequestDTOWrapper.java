package org.apiwiz.model;

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
public class RequestDTOWrapper {
    private ApiMethod apiMethod;
    private RequestDTO requestDTO;
    private int timeout;
}
