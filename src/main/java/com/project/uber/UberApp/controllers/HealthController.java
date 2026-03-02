package com.project.uber.UberApp.controllers;

import com.project.uber.UberApp.advices.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<ApiResponse<String>> getHealth() {
        return ResponseEntity.ok(new ApiResponse<>("Ok"));
    }
}
