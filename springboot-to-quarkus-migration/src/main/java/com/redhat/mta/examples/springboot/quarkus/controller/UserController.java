package com.redhat.mta.examples.springboot.quarkus.controller;

import com.redhat.mta.examples.springboot.quarkus.dto.UserCreateRequest;
import com.redhat.mta.examples.springboot.quarkus.dto.UserResponse;
import com.redhat.mta.examples.springboot.quarkus.dto.UserUpdateRequest;
import com.redhat.mta.examples.springboot.quarkus.model.User;
import com.redhat.mta.examples.springboot.quarkus.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User Controller demonstrating Spring Web patterns for Quarkus migration.
 * 
 * Migration to Quarkus:
 * - Replace @RestController with @Path
 * - Replace @RequestMapping with @Path
 * - Replace @GetMapping with @GET + @Path
 * - Replace @PostMapping with @POST + @Path
 * - Replace @PutMapping with @PUT + @Path
 * - Replace @DeleteMapping with @DELETE + @Path
 * - Replace @PathVariable with @PathParam
 * - Replace @RequestParam with @QueryParam
 * - Replace @RequestBody with no annotation (automatic in Quarkus)
 * - Replace @RequestHeader with @HeaderParam
 * - Replace @CookieValue with @CookieParam
 * - Replace ResponseEntity with JAX-RS Response
 * - Replace HttpStatus with Response.Status
 * - Replace @PreAuthorize with @RolesAllowed
 * - Replace @CrossOrigin with Quarkus CORS configuration
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
@Validated
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * Get all users with pagination
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean active,
            @RequestHeader(value = "X-Client-Version", required = false) String clientVersion) {
        
        logger.debug("Getting all users with pagination: {}, clientVersion: {}", pageable, clientVersion);
        
        Page<User> users;
        if (firstName != null || lastName != null || email != null || active != null) {
            users = userService.searchUsers(firstName, lastName, email, active, pageable);
        } else {
            users = userService.findAll(pageable);
        }
        
        Page<UserResponse> userResponses = users.map(this::convertToResponse);
        return ResponseEntity.ok(userResponses);
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == authentication.principal.id)")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable @NotNull Long id,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage) {
        
        logger.debug("Getting user by ID: {}, language: {}", id, acceptLanguage);
        
        User user = userService.findById(id);
        UserResponse response = convertToResponse(user);
        
        return ResponseEntity.ok()
                .header("Content-Language", acceptLanguage)
                .body(response);
    }

    /**
     * Get user by username
     */
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserByUsername(
            @PathVariable String username) {
        
        logger.debug("Getting user by username: {}", username);
        
        return userService.findByUsername(username)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create new user
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserCreateRequest request,
            @RequestHeader(value = "X-Request-ID", required = false) String requestId) {
        
        logger.info("Creating new user: {}, requestId: {}", request.getUsername(), requestId);
        
        User user = convertFromCreateRequest(request);
        User createdUser = userService.createUser(user);
        UserResponse response = convertToResponse(createdUser);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("X-Request-ID", requestId)
                .body(response);
    }

    /**
     * Update existing user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == authentication.principal.id)")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable @NotNull Long id,
            @Valid @RequestBody UserUpdateRequest request,
            @RequestHeader(value = "If-Match", required = false) String ifMatch) {
        
        logger.info("Updating user: {}, ifMatch: {}", id, ifMatch);
        
        User user = convertFromUpdateRequest(request);
        user.setId(id);
        
        User updatedUser = userService.updateUser(user);
        UserResponse response = convertToResponse(updatedUser);
        
        return ResponseEntity.ok()
                .header("ETag", "\"" + updatedUser.getUpdatedAt().toString() + "\"")
                .body(response);
    }

    /**
     * Update user status (activate/deactivate)
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable @NotNull Long id,
            @RequestParam boolean active) {
        
        logger.info("Updating user status: {} to {}", id, active);
        
        User updatedUser = userService.updateUserStatus(id, active);
        UserResponse response = convertToResponse(updatedUser);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @PathVariable @NotNull Long id,
            @RequestHeader(value = "X-Confirm-Delete", defaultValue = "false") boolean confirmDelete) {
        
        if (!confirmDelete) {
            return ResponseEntity.badRequest().build();
        }
        
        logger.info("Deleting user: {}", id);
        userService.deleteUser(id);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Add role to user
     */
    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> addRoleToUser(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Long roleId) {
        
        logger.info("Adding role {} to user {}", roleId, userId);
        
        User updatedUser = userService.addRoleToUser(userId, roleId);
        UserResponse response = convertToResponse(updatedUser);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Remove role from user
     */
    @DeleteMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeRoleFromUser(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Long roleId) {
        
        logger.info("Removing role {} from user {}", roleId, userId);
        userService.removeRoleFromUser(userId, roleId);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Get active users
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getActiveUsers() {
        logger.debug("Getting all active users");
        
        List<User> activeUsers = userService.findActiveUsers();
        List<UserResponse> responses = activeUsers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Get users by role
     */
    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsersByRole(
            @PathVariable String roleName) {
        
        logger.debug("Getting users by role: {}", roleName);
        
        List<User> users = userService.findUsersByRole(roleName);
        List<UserResponse> responses = users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Get user count
     */
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getUserCount(
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        
        long count = activeOnly ? userService.countActiveUsers() : userService.findAll(Pageable.unpaged()).getTotalElements();
        return ResponseEntity.ok(count);
    }

    /**
     * Send welcome email (async operation)
     */
    @PostMapping("/{id}/welcome-email")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('ADMIN')")
    public void sendWelcomeEmail(@PathVariable @NotNull Long id) {
        logger.info("Triggering welcome email for user: {}", id);
        userService.sendWelcomeEmailAsync(id);
    }

    // Utility methods for conversion
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setFullName(user.getFullName());
        response.setBio(user.getBio());
        response.setActive(user.getActive());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        if (user.getRoles() != null) {
            response.setRoles(user.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toSet()));
        }
        
        return response;
    }

    private User convertFromCreateRequest(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(request.getPassword());
        user.setBio(request.getBio());
        return user;
    }

    private User convertFromUpdateRequest(UserUpdateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBio(request.getBio());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(request.getPassword());
        }
        return user;
    }
}
