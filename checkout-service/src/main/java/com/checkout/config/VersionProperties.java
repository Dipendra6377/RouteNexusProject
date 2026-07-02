package com.checkout.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VersionProperties {

    @Value("${service.version}")
    private String version;

    public String getVersion() {
        return version;
    }
}