package org.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ping")
public class PingController {

    @GetMapping
    public ResponseEntity<Map<String, String>> ping() {
        Map<String, String> response = new HashMap<>();
        // ✅ Change "pong" to "hello" to match the test's expectation
        response.put("message", "Hello, World!");
        return ResponseEntity.ok(response);
    }
}