package com.checkout.controller;

import com.checkout.model.CheckoutResponse;
import com.checkout.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @GetMapping("/checkout")
    public CheckoutResponse checkout() {
        return checkoutService.checkout();
    }
}