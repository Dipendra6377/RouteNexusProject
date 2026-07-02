package com.routing.routing.strategy;

import com.routing.circuit.CircuitBreakerService;
import com.routing.routing.cache.RoutingCacheService;
import com.routing.persistant.entity.ServiceInstanceEntity;
import com.routing.persistant.mapper.ServiceInstanceMapper;
import com.routing.routing.model.RouteRequest;
import com.routing.routing.model.ServiceInstance;

import com.routing.persistant.repository.ServiceInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class WeightedRoutingStrategy implements RoutingStrategy {

    private final ServiceInstanceRepository repository;

    private final CircuitBreakerService circuitBreakerService;

    private final ServiceInstanceMapper mapper;

    private final RoutingCacheService cacheService;

    @Override
    public ServiceInstance route(RouteRequest request) {

        List<ServiceInstance> instances =
                cacheService.get(request.getServiceName());

        if (instances == null) {

            List<ServiceInstanceEntity> entities =
                    repository.findByServiceNameAndActiveTrue(
                            request.getServiceName());

            if (entities.isEmpty()) {
                throw new RuntimeException("No active instance found.");
            }

            instances = mapper.toModelList(entities);

            cacheService.put(
                    request.getServiceName(),
                    instances);

            System.out.println("Loaded from PostgreSQL");

        } else {

            System.out.println("Loaded from Redis");
        }

        // Remove instances whose circuit is OPEN
        instances = instances.stream()
                .filter(instance ->
                        circuitBreakerService.allowRequest(
                                instance.getUrl()))
                .toList();

        if (instances.isEmpty()) {

            throw new RuntimeException(
                    "No healthy instance available.");

        }

        return selectByWeight(instances);
    }
    private ServiceInstance selectByWeight(
            List<ServiceInstance> instances) {

        int totalWeight = instances.stream()
                .mapToInt(ServiceInstance::getWeight)
                .sum();

        int random =
                ThreadLocalRandom.current()
                        .nextInt(totalWeight);

        int cumulative = 0;

        for (ServiceInstance instance : instances) {

            cumulative += instance.getWeight();

            if (random < cumulative) {
                return instance;
            }
        }

        throw new IllegalStateException("Routing failed.");
    }

}