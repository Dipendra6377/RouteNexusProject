package com.routing.routing.controller;

import com.routing.routing.dto.CheckoutResponse;
import com.routing.routing.service.RouterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RouterController {

    private final RouterService routerService;

    @GetMapping("/checkout")
    public CheckoutResponse checkout() {

        return routerService.checkout();
    }

}