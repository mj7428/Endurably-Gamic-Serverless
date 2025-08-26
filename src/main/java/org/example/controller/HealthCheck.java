package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheck {
    
    @GetMapping("/healthcheck")
    public String healthcheck(){
        return "Server is running";
    }
}
