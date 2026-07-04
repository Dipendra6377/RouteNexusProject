package com.routing.pubsub;

import com.routing.circuit.CircuitBreaker;
import com.routing.circuit.CircuitBreakerManager;
import com.routing.circuit.CircuitState;
import com.routing.routing.cache.RoutingCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoutingEventSubscriber {

    private final RoutingCacheService cacheService;

    private final CircuitBreakerManager manager;

    public void receiveMessage(String message) {

        System.out.println();
        System.out.println("========== SUB ==========");
        System.out.println(message);
        System.out.println("=========================");

        String[] split =
                message.split(":");

        String event = split[0];
        String service = split[1];

        if ("CACHE_CLEAR".equals(event)) {

            cacheService.evictLocal(service);

        }

        if ("CIRCUIT_OPEN".equals(event)) {

            manager.getCircuit(service)
                    .setState(CircuitState.OPEN);

            System.out.println("Distributed Circuit OPEN");

            return;
        }

        if ("CIRCUIT_CLOSE".equals(event)) {

            CircuitBreaker breaker =
                    manager.getCircuit(service);

            breaker.setFailureCount(0);
            breaker.setState(CircuitState.CLOSED);

            System.out.println("Distributed Circuit CLOSED");

            return;
        }

    }

}