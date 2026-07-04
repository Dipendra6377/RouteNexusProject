package com.routing.routing.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.routing.monitoring.MetricsService;
import com.routing.pubsub.RoutingEventPublisher;
import com.routing.routing.model.ServiceInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutingCacheService {

    private static final Duration TTL = Duration.ofMinutes(5);

    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    private final MetricsService metricsService;

    private final RoutingEventPublisher publisher;

    public List<ServiceInstance> get(String serviceName) {

        String json =
                redisTemplate.opsForValue().get(serviceName);

        if (json == null) {

            System.out.println("Redis Cache MISS");

            metricsService.cacheMiss();

            return null;
        }

        System.out.println("Redis Cache HIT");

        metricsService.cacheHit();

        try {

            return objectMapper.readValue(
                    json,
                    new TypeReference<List<ServiceInstance>>() {
                    });

        } catch (JsonProcessingException e) {

            throw new RuntimeException(e);
        }
    }

    public void put(
            String serviceName,
            List<ServiceInstance> instances) {

        try {

            String json =
                    objectMapper.writeValueAsString(instances);

            redisTemplate.opsForValue()
                    .set(serviceName, json, TTL);

        } catch (JsonProcessingException e) {

            throw new RuntimeException(e);
        }

    }

    public void evict(String serviceName) {

        evictLocal(serviceName);

        publisher.publish(
                "CACHE_CLEAR",
                serviceName);
    }

    public void evictLocal(String serviceName) {

        redisTemplate.delete(serviceName);

        System.out.println("Local Cache Cleared");
    }
    public void clear() {

        redisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushDb();
    }

}