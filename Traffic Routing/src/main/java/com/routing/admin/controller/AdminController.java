package com.routing.admin.controller;

import com.routing.admin.dto.UpdateWeightRequest;
import com.routing.admin.service.AdminService;
import com.routing.routing.cache.RoutingCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final RoutingCacheService cacheService;

    @PutMapping("/weight")
    public String updateWeight(
            @RequestBody UpdateWeightRequest request) {

        adminService.updateWeight(request);

        return "Weight Updated Successfully";

    }

    @PostMapping("/cache/clear")
    public String clear() {

        cacheService.evict("checkout-service");

        return "Cache Cleared";

    }
}