package com.example.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/api/hello")
    public Map<String, Object> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from Gateway Demo");
        response.put("status", "success");
        response.put("service", "Gateway Demo Service");
        return response;
    }

    @GetMapping("/api/status")
    public Map<String, String> status() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Gateway is running");
        return response;
    }

}