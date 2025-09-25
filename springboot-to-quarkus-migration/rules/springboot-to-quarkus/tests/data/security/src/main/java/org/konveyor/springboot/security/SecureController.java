package org.konveyor.springboot.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/secure")
public class SecureController {

    @GetMapping("/profile")
    public UserProfile getProfile(Authentication authentication) {
        String username = authentication.getName();
        return new UserProfile(username);
    }

    @GetMapping("/me")
    public User getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userService.findById(userPrincipal.getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @Secured("ROLE_USER")
    @GetMapping("/user-data")
    public String getUserData() {
        return "User specific data";
    }
}
