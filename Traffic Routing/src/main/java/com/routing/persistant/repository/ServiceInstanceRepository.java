package com.routing.persistant.repository;

import com.routing.persistant.entity.ServiceInstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceInstanceRepository
        extends JpaRepository<ServiceInstanceEntity, Long> {

    List<ServiceInstanceEntity> findByServiceNameAndActiveTrue(
            String serviceName);

    Optional<ServiceInstanceEntity> findByServiceNameAndVersion(
            String serviceName,
            String version);

}