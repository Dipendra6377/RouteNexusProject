package com.routing.discovery.scheduler;

import com.routing.persistant.entity.ServiceInstanceEntity;
import com.routing.persistant.repository.ServiceInstanceRepository;
import com.routing.routing.cache.RoutingCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HealthCheckScheduler {

    private final ServiceInstanceRepository repository;
    private final RoutingCacheService cacheService;
    private final RestClient restClient;

    @Scheduled(fixedDelay = 10000)
    public void checkHealth() {

        List<ServiceInstanceEntity> services = repository.findAll();

        System.out.println("--------------------------");
        System.out.println("Running Health Check...");
        System.out.println("--------------------------");

        for (ServiceInstanceEntity service : services) {

            String healthUrl = service.getUrl() + "/health";

            try {

                restClient.get()
                        .uri(healthUrl)
                        .retrieve()
                        .toBodilessEntity();

                // Service is UP
                if (!Boolean.TRUE.equals(service.getActive())) {

                    service.setActive(true);
                    repository.save(service);

                    cacheService.evict(service.getServiceName());

                    System.out.println(service.getVersion()
                            + " became UP. Cache evicted.");
                } else {

                    System.out.println(service.getVersion()
                            + " is UP");
                }

            } catch (Exception e) {

                // Service is DOWN
                if (Boolean.TRUE.equals(service.getActive())) {

                    service.setActive(false);
                    repository.save(service);

                    cacheService.evict(service.getServiceName());

                    System.out.println(service.getVersion()
                            + " became DOWN. Cache evicted.");
                } else {

                    System.out.println(service.getVersion()
                            + " is DOWN");
                }
            }
        }
    }
}