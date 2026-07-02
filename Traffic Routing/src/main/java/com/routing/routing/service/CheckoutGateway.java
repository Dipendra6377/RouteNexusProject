package com.routing.routing.service;

import com.routing.routing.model.ServiceInstance;
import com.routing.routing.dto.CheckoutResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class CheckoutGateway {

    private final RestClient restClient;

    public CheckoutResponse forward(ServiceInstance instance) {

        return restClient.get()

                .uri(instance.getUrl() + "/checkout")

                .retrieve()

                .body(CheckoutResponse.class);
    }
}