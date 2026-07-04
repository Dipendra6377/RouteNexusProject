package com.checkout.controller;

import com.checkout.config.VersionProperties;
import com.checkout.model.CheckoutResponse;
import com.checkout.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final VersionProperties versionProperties;

    public CheckoutController(CheckoutService checkoutService, VersionProperties versionProperties) {
        this.checkoutService = checkoutService;
        this.versionProperties = versionProperties;
    }

    @GetMapping("/checkout")
    public CheckoutResponse checkout() {

        return checkoutService.checkout();
    }
}