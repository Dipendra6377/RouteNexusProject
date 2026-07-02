package com.routing.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final Counter totalRequests;

    private final Counter retryCounter;

    private final Counter failoverCounter;

    private final Counter cacheHitCounter;

    private final Counter cacheMissCounter;

    private final Counter circuitOpenCounter;

    private final Counter circuitCloseCounter;

    private final Timer routingTimer;

    private final MeterRegistry registry;

    public MetricsService(MeterRegistry registry) {

        this.registry = registry;

        totalRequests =
                Counter.builder("router_requests_total")
                        .description("Total routed requests")
                        .register(registry);

        retryCounter =
                Counter.builder("router_retry_total")
                        .description("Retry attempts")
                        .register(registry);

        failoverCounter =
                Counter.builder("router_failover_total")
                        .description("Failovers")
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
                        .register(registry);

        circuitCloseCounter =
                Counter.builder("router_circuit_close_total")
                        .register(registry);

        routingTimer =
                Timer.builder("router_latency")
                        .description("Routing latency")
                        .register(registry);
    }

    public void incrementRequest() {
        totalRequests.increment();
    }

    public void incrementRetry() {
        retryCounter.increment();
    }

    public void incrementFailover() {
        failoverCounter.increment();
    }

    public void cacheHit() {
        cacheHitCounter.increment();
    }

    public void cacheMiss() {
        cacheMissCounter.increment();
    }

    public void circuitOpen() {
        circuitOpenCounter.increment();
    }

    public void circuitClose() {
        circuitCloseCounter.increment();
    }

    public Timer.Sample startTimer() {
        return Timer.start(registry);
    }

    public void stopTimer(Timer.Sample sample) {
        sample.stop(routingTimer);
    }

    public void incrementVersion(String version) {

        Counter.builder("router_version_requests_total")
                .tag("version", version)
                .register(registry)
                .increment();

    }

}