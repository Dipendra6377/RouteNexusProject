package com.routing.pubsub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoutingEventPublisher {

    private static final String CHANNEL = "routing-events";

    private final StringRedisTemplate redisTemplate;

    public void publish(String eventType,
                        String serviceName) {

        String message =
                eventType + ":" + serviceName;

        redisTemplate.convertAndSend(
                CHANNEL,
                message);

        System.out.println();
        System.out.println("========== PUB ==========");
        System.out.println(message);
        System.out.println("=========================");
    }

}