package com.routing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrafficRouterApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                TrafficRouterApplication.class,
                args);

    }

}