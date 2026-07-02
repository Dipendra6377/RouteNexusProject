package com.routing.routing.retry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RetryPolicy {

    @Value("${routing.retry-count:2}")
    private int retryCount;

    public int getRetryCount() {
        return retryCount;
    }
}