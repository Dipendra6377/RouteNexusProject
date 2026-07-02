package com.routing.admin.dto;

import lombok.Data;

@Data
public class UpdateWeightRequest {

    private String serviceName;
    private String version;
    private Integer weight;

}