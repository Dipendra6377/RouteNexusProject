package com.routing.circuit;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class CircuitBreakerManager {

    private final ConcurrentMap<String, CircuitBreaker> circuitMap =
            new ConcurrentHashMap<>();

    public CircuitBreaker getCircuit(String serviceUrl) {

        return circuitMap.computeIfAbsent(
                serviceUrl,
                url -> new CircuitBreaker());

    }

}