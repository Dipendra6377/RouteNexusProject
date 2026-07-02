package com.routing.routing.strategy;

import com.routing.routing.model.RouteRequest;
import com.routing.routing.model.ServiceInstance;

public interface RoutingStrategy {

    ServiceInstance route(RouteRequest request);

}