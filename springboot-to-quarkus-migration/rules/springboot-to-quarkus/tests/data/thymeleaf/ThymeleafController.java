package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ThymeleafController {
    
    @GetMapping("/users")
    public String listUsers(Model model) {
        // This should trigger the Thymeleaf to Qute controller rule
        model.addAttribute("users", userService.findAll());
        model.addAttribute("title", "User List");
        return "users/list";
    }
    
    @GetMapping("/users/{id}")
    public String showUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        return "users/detail";
    }
}








