package com.fireboxsys.supermarkets.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health Check", description = "Endpoint to verify the API status")
public class HealthController {

    @GetMapping
    @Operation(summary = "Check API health", description = "Returns the operational status of the API. Useful for waking up the server on Render.")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "Supermarkets API is up and running!",
                "environment", "Production"
        ));
    }
}