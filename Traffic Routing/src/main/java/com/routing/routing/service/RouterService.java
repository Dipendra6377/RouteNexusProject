package com.routing.routing.service;

import com.routing.circuit.CircuitBreakerService;
import com.routing.monitoring.MetricsService;
import com.routing.routing.dto.CheckoutResponse;
import com.routing.routing.model.RouteRequest;
import com.routing.routing.model.ServiceInstance;
import com.routing.routing.retry.RetryPolicy;
import com.routing.routing.strategy.RoutingStrategy;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouterService {

    private final RoutingStrategy routingStrategy;

    private final CheckoutGateway checkoutGateway;

    private final RetryPolicy retryPolicy;

    private final CircuitBreakerService circuitBreakerService;

    private final MetricsService metricsService;

    public CheckoutResponse checkout() {

        // Increment total request count
        metricsService.incrementRequest();

        // Start latency timer
        Timer.Sample timer = metricsService.startTimer();

        RouteRequest request = new RouteRequest();
        request.setServiceName("checkout-service");

        List<String> attempted = new ArrayList<>();

        try {

            for (int i = 0; i <= retryPolicy.getRetryCount(); i++) {

                ServiceInstance instance =
                        routingStrategy.route(request, attempted);

                attempted.add(instance.getUrl());

                System.out.println();
                System.out.println("==============================");
                System.out.println("Attempt : " + (i + 1));
                System.out.println("Routing to : " + instance.getVersion());
                System.out.println("==============================");

                try {

                    CheckoutResponse response =
                            checkoutGateway.forward(instance);

                    // Circuit becomes healthy
                    circuitBreakerService.recordSuccess(instance.getUrl());

                    // Metrics
                    metricsService.incrementVersion(instance.getVersion());

                    System.out.println("Request completed successfully.");

                    return response;

                } catch (Exception ex) {

                    // Circuit failure
                    circuitBreakerService.recordFailure(instance.getUrl());

                    // Retry metrics
                    metricsService.incrementRetry();

                    // Count failover only if another retry is possible
                    if (i < retryPolicy.getRetryCount()) {
                        metricsService.incrementFailover();
                    }

                    System.out.println("Retrying with another instance...");

                }
            }

            throw new RuntimeException("All retry attempts failed.");

        } finally {

            // Stop latency timer
            metricsService.stopTimer(timer);

        }
    }
}