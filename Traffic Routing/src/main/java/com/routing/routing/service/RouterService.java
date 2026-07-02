package com.routing.routing.service;

import com.routing.circuit.CircuitBreakerService;
import com.routing.routing.model.RouteRequest;
import com.routing.routing.model.ServiceInstance;
import com.routing.routing.dto.CheckoutResponse;
import com.routing.routing.service.CheckoutGateway;
import com.routing.routing.strategy.RoutingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouterService {

    private final RoutingStrategy routingStrategy;

    private final CheckoutGateway checkoutGateway;

    private final CircuitBreakerService circuitBreakerService;

    public CheckoutResponse checkout() {

        RouteRequest request = new RouteRequest();
        request.setServiceName("checkout-service");

        ServiceInstance instance =
                routingStrategy.route(request);
        System.out.println("Routing to : " + instance.getVersion());
        try {

            CheckoutResponse response =
                    checkoutGateway.forward(instance);

            circuitBreakerService.recordSuccess(
                    instance.getUrl());

            return response;

        } catch (Exception ex) {

            circuitBreakerService.recordFailure(
                    instance.getUrl());

            throw ex;

        }

    }

}