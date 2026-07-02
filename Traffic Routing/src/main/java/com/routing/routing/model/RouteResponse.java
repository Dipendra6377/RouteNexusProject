package com.routing.routing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponse {

    private String serviceName;

    private String version;

    private String url;

    private String message;

}