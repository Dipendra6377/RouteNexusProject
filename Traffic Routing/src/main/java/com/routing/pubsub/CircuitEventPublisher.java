package com.routing.pubsub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CircuitEventPublisher {

    private static final String CHANNEL = "routing-events";

    private final StringRedisTemplate redisTemplate;

    public void publishOpen(String url) {

        String event = "CIRCUIT_OPEN:" + url;

        redisTemplate.convertAndSend(CHANNEL, event);

        System.out.println();
        System.out.println("========== PUB ==========");
        System.out.println(event);
        System.out.println("=========================");
    }

    public void publishClose(String url) {

        String event = "CIRCUIT_CLOSE:" + url;

        redisTemplate.convertAndSend(CHANNEL, event);

        System.out.println();
        System.out.println("========== PUB ==========");
        System.out.println(event);
        System.out.println("=========================");
    }

}