package com.routing.routing.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public List<ServiceInstance> get(String serviceName) {

        String json =
                redisTemplate.opsForValue().get(serviceName);

        if (json == null) {
            return null;
        }

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

            redisTemplate.opsForValue().set(
                    serviceName,
                    json,
                    TTL);

        } catch (JsonProcessingException e) {

            throw new RuntimeException(e);
        }

    }

    public void evict(String serviceName) {

        redisTemplate.delete(serviceName);

    }
    public void clear() {

        redisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushDb();
    }

}