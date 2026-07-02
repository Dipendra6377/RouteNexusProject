package com.routing.circuit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CircuitBreakerService {

    private static final int FAILURE_THRESHOLD = 5;
    private static final long OPEN_TIMEOUT = 30000;

    private final CircuitBreakerManager manager;

    public boolean allowRequest(String serviceUrl) {

        CircuitBreaker breaker =
                manager.getCircuit(serviceUrl);

        System.out.println("--------------------------------");
        System.out.println("Checking Circuit : " + serviceUrl);
        System.out.println("Current State    : " + breaker.getState());
        System.out.println("Failure Count    : " + breaker.getFailureCount());

        if (breaker.getState() == CircuitState.CLOSED) {

            System.out.println("Request Allowed (CLOSED)");
            System.out.println("--------------------------------");

            return true;
        }

        if (breaker.getState() == CircuitState.OPEN) {

            long elapsed =
                    System.currentTimeMillis()
                            - breaker.getLastFailureTime();

            System.out.println("Circuit OPEN");
            System.out.println("Elapsed : " + elapsed);

            if (elapsed >= OPEN_TIMEOUT) {

                breaker.setState(CircuitState.HALF_OPEN);

                System.out.println("Moving to HALF_OPEN");
                System.out.println("--------------------------------");

                return true;
            }

            System.out.println("Skipping Request");
            System.out.println("--------------------------------");

            return false;
        }

        System.out.println("HALF_OPEN -> Allowing Request");
        System.out.println("--------------------------------");

        return true;
    }

    public void recordSuccess(String serviceUrl) {

        CircuitBreaker breaker =
                manager.getCircuit(serviceUrl);

        System.out.println("========== SUCCESS ==========");
        System.out.println("Service : " + serviceUrl);

        breaker.setFailureCount(0);
        breaker.setState(CircuitState.CLOSED);

        System.out.println("Failure Count Reset");
        System.out.println("State -> CLOSED");
        System.out.println("=============================");
    }

    public void recordFailure(String serviceUrl) {

        CircuitBreaker breaker =
                manager.getCircuit(serviceUrl);

        System.out.println("========== FAILURE ==========");
        System.out.println("Service : " + serviceUrl);

        breaker.setFailureCount(
                breaker.getFailureCount() + 1);

        System.out.println("Failure Count : "
                + breaker.getFailureCount());

        if (breaker.getFailureCount() >= FAILURE_THRESHOLD) {

            breaker.setState(CircuitState.OPEN);

            breaker.setLastFailureTime(
                    System.currentTimeMillis());

            System.out.println("Circuit OPENED");
        }

        System.out.println("=============================");
    }
}