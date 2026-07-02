package com.routing.admin.service;

import com.routing.routing.cache.RoutingCacheService;
import com.routing.persistant.entity.ServiceInstanceEntity;
import com.routing.admin.dto.UpdateWeightRequest;
import com.routing.persistant.repository.ServiceInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ServiceInstanceRepository repository;
    private final RoutingCacheService cacheService;

    @Transactional
    public void updateWeight(UpdateWeightRequest request) {

        ServiceInstanceEntity entity =
                repository.findByServiceNameAndVersion(
                                request.getServiceName(),
                                request.getVersion())
                        .orElseThrow(() ->
                                new RuntimeException("Service instance not found"));

        entity.setWeight(request.getWeight());

        // Optional because of dirty checking,
        // but we'll keep it for clarity.
        repository.save(entity);

        cacheService.evict(request.getServiceName());

        System.out.println("Cache cleared.");

    }
}