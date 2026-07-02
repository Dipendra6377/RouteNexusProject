package com.routing.routing.strategy;

import com.routing.routing.model.RouteRequest;
import com.routing.routing.model.ServiceInstance;

import java.util.List;

public interface RoutingStrategy {

    ServiceInstance route(RouteRequest request);

    ServiceInstance route(
            RouteRequest request,
            List<String> excludedUrls);

}