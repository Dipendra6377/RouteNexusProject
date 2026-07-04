package com.routing.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MetricsService {

    private final MeterRegistry registry;

    // ===========================
    // Counters
    // ===========================

    private final Counter totalRequests;

    private final Counter retryCounter;

    private final Counter failoverCounter;

    private final Counter cacheHitCounter;

    private final Counter cacheMissCounter;

    private final Counter circuitOpenCounter;

    private final Counter circuitCloseCounter;

    private final Counter rateLimitAllowedCounter;

    private final Counter rateLimitRejectedCounter;

    // ===========================
    // Gauges
    // ===========================

    private final AtomicInteger remainingTokens =
            new AtomicInteger();

    // ===========================
    // Timer
    // ===========================

    private final Timer routingTimer;

    // ===========================
    // Dynamic Counters
    // ===========================

    private final ConcurrentHashMap<String, Counter> versionCounters =
            new ConcurrentHashMap<>();

    public MetricsService(MeterRegistry registry) {

        this.registry = registry;

        totalRequests =
                Counter.builder("router_requests_total")
                        .description("Total routed requests")
                        .register(registry);

        retryCounter =
                Counter.builder("router_retry_total")
                        .description("Total retry attempts")
                        .register(registry);

        failoverCounter =
                Counter.builder("router_failover_total")
                        .description("Total failovers")
                        .register(registry);

        cacheHitCounter =
                Counter.builder("router_cache_hit_total")
                        .description("Redis cache hits")
                        .register(registry);

        cacheMissCounter =
                Counter.builder("router_cache_miss_total")
                        .description("Redis cache misses")
                        .register(registry);

        circuitOpenCounter =
                Counter.builder("router_circuit_open_total")
                        .description("Circuit opened")
                        .register(registry);

        circuitCloseCounter =
                Counter.builder("router_circuit_close_total")
                        .description("Circuit closed")
                        .register(registry);

        rateLimitAllowedCounter =
                Counter.builder("rate_limit_allowed_total")
                        .description("Allowed requests")
                        .register(registry);

        rateLimitRejectedCounter =
                Counter.builder("rate_limit_rejected_total")
                        .description("Rejected requests")
                        .register(registry);

        Gauge.builder(
                        "rate_limit_remaining_tokens",
                        remainingTokens,
                        AtomicInteger::get)
                .description("Remaining tokens in bucket")
                .register(registry);

        routingTimer =
                Timer.builder("router_request_latency_seconds")
                        .description("Routing latency")
                        .register(registry);
    }

    // ==================================================
    // Routing Metrics
    // ==================================================

    public void incrementRequest() {

        totalRequests.increment();
    }

    public void incrementRetry() {

        retryCounter.increment();
    }

    public void incrementFailover() {

        failoverCounter.increment();
    }

    // ==================================================
    // Cache Metrics
    // ==================================================

    public void cacheHit() {

        cacheHitCounter.increment();
    }

    public void cacheMiss() {

        cacheMissCounter.increment();
    }

    // ==================================================
    // Circuit Breaker Metrics
    // ==================================================

    public void circuitOpen() {

        circuitOpenCounter.increment();
    }

    public void circuitClose() {

        circuitCloseCounter.increment();
    }

    // ==================================================
    // Rate Limiter Metrics
    // ==================================================

    public void rateLimitAllowed(int remainingTokens) {

        rateLimitAllowedCounter.increment();

        this.remainingTokens.set(remainingTokens);
    }

    public void rateLimitRejected() {

        rateLimitRejectedCounter.increment();
    }

    // ==================================================
    // Timer
    // ==================================================

    public Timer.Sample startTimer() {

        return Timer.start(registry);
    }

    public void stopTimer(Timer.Sample sample) {

        sample.stop(routingTimer);
    }

    // ==================================================
    // Backend Version Metrics
    // ==================================================

    public void incrementVersion(String version) {

        versionCounters
                .computeIfAbsent(
                        version,
                        v -> Counter.builder("router_backend_requests_total")
                                .description("Requests routed to backend instances")
                                .tag("version", v)
                                .register(registry))
                .increment();
    }
}