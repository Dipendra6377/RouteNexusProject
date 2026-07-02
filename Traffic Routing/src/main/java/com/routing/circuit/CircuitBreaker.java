package com.routing.circuit;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CircuitBreaker {

    private CircuitState state = CircuitState.CLOSED;

    private int failureCount = 0;

    private long lastFailureTime = 0;

}