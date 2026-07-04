package com.routing.ratelimiter;

import com.routing.monitoring.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class DistributedRateLimiterService {

    private static final int CAPACITY = 20;

    // Tokens added every second
    private static final int REFILL_RATE = 10;

    private final StringRedisTemplate redisTemplate;

    private final DefaultRedisScript<Long> rateLimitScript;

    private final MetricsService metricsService;

    public boolean allowRequest(String clientIp) {

        String key = "bucket:" + clientIp;

        Long remainingTokens =
                redisTemplate.execute(
                        rateLimitScript,
                        Collections.singletonList(key),
                        String.valueOf(CAPACITY),
                        String.valueOf(REFILL_RATE));

        if (remainingTokens == null) {

            System.out.println();
            System.out.println("========== RATE LIMIT ==========");
            System.out.println("Redis Lua execution failed");
            System.out.println("===============================");

            throw new RuntimeException(
                    "Redis Rate Limiter Failed");
        }

        if (remainingTokens >= 0) {

            // -------- Grafana Metrics --------

            metricsService.rateLimitAllowed(
                    remainingTokens.intValue());

            // -------- Console Logs --------

            System.out.println();
            System.out.println("========== DISTRIBUTED RATE LIMIT ==========");
            System.out.println("Client IP        : " + clientIp);
            System.out.println("Redis Key        : " + key);
            System.out.println("Capacity         : " + CAPACITY);
            System.out.println("Refill Rate      : " + REFILL_RATE + " tokens/sec");
            System.out.println("Request Status   : ALLOWED");
            System.out.println("Remaining Tokens : " + remainingTokens);
            System.out.println("============================================");

            return true;
        }

        // -------- Grafana Metrics --------

        metricsService.rateLimitRejected();

        // -------- Console Logs --------

        System.out.println();
        System.out.println("========== DISTRIBUTED RATE LIMIT ==========");
        System.out.println("Client IP      : " + clientIp);
        System.out.println("Redis Key      : " + key);
        System.out.println("Request Status : REJECTED");
        System.out.println("Reason         : No Tokens Available");
        System.out.println("============================================");

        return false;
    }
}