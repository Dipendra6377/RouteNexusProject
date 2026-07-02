package com.checkout.controller;

import com.checkout.config.VersionProperties;
import com.checkout.model.HealthResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final VersionProperties versionProperties;

    public HealthController(VersionProperties versionProperties) {
        this.versionProperties = versionProperties;
    }

    @GetMapping("/health")
    public HealthResponse health() {

        return new HealthResponse(
                "UP",
                versionProperties.getVersion()
        );
    }
}