package com.example.petclinic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {

    @Value("${app.environment:development}")
    private String environment;

    @Value("${app.message:Welcome to PetClinic!}")
    private String message;

    @Value("${server.port:8080}")
    private String port;

    @GetMapping("/")
    public Map<String, Object> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("environment", environment);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("port", port);
        response.put("status", "running");
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("environment", environment);
        return response;
    }

    @GetMapping("/pets")
    public Map<String, Object> pets() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "PetClinic - Caring for your pets!");
        response.put("environment", environment);
        response.put("availableServices", new String[]{"Veterinary Care", "Grooming", "Boarding"});
        return response;
    }
}
