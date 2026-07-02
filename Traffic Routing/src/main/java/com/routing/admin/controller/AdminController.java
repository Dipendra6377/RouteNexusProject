package com.routing.admin.controller;

import com.routing.admin.dto.UpdateWeightRequest;
import com.routing.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PutMapping("/weight")
    public String updateWeight(
            @RequestBody UpdateWeightRequest request) {

        adminService.updateWeight(request);

        return "Weight Updated Successfully";

    }

}