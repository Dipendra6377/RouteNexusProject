package com.routing.persistant.mapper;

import com.routing.persistant.entity.ServiceInstanceEntity;
import com.routing.routing.model.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceInstanceMapper {

    public ServiceInstance toModel(ServiceInstanceEntity entity) {

        ServiceInstance instance = new ServiceInstance();

        instance.setServiceName(entity.getServiceName());
        instance.setVersion(entity.getVersion());
        instance.setUrl(entity.getUrl());
        instance.setActive(entity.getActive());
        instance.setWeight(entity.getWeight());

        return instance;
    }

    public List<ServiceInstance> toModelList(
            List<ServiceInstanceEntity> entities) {

        return entities.stream()
                .map(this::toModel)
                .toList();
    }

}