package com.checkout.service;

import com.checkout.config.VersionProperties;
import com.checkout.model.CheckoutResponse;
import org.springframework.stereotype.Service;

@Service
public class CheckoutService {

    private final VersionProperties versionProperties;

    public CheckoutService(VersionProperties versionProperties) {
        this.versionProperties = versionProperties;
    }

    public CheckoutResponse checkout() {

        return new CheckoutResponse(
                versionProperties.getVersion(),
                "Checkout Successful"
        );
    }
}