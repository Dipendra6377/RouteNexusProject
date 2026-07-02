package com.routing.routing.model;

import lombok.*;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInstance {

    private String serviceName;
    private String version;
    private String url;
    private Integer weight;
    private boolean active;
}