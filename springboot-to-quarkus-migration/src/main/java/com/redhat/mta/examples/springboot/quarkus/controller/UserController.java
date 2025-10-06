package com.redhat.mta.examples.springboot.quarkus.controller;

import com.redhat.mta.examples.springboot.quarkus.dto.UserCreateRequest;
import com.redhat.mta.examples.springboot.quarkus.dto.UserResponse;
import com.redhat.mta.examples.springboot.quarkus.dto.UserUpdateRequest;
import com.redhat.mta.examples.springboot.quarkus.model.User;
import com.redhat.mta.examples.springboot.quarkus.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User Controller demonstrating Spring Web patterns for Quarkus migration.
 */
@Path("/api/users")
@Validated
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Inject
    UserService userService;

    /**
     * Get all users with pagination
     */
    @GET
    @RolesAllowed({"ADMIN", "USER"})
    public Response getAllUsers(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("firstName") String firstName,
            @QueryParam("lastName") String lastName,
            @QueryParam("email") String email,
            @QueryParam("active") Boolean active,
            @HeaderParam("X-Client-Version") String clientVersion) {
        
        logger.debug("Getting all users with pagination: page={}, size={}, clientVersion={}", page, size, clientVersion);
        
        List<User> users;
        if (firstName != null || lastName != null || email != null || active != null) {
            users = userService.searchUsers(firstName, lastName, email, active, page, size);
        } else {
            users = userService.findAll(page, size);
        }
        
        List<UserResponse> userResponses = users.stream().map(this::convertToResponse).collect(Collectors.toList());
        return Response.ok(userResponses).build();
    }

    /**
     * Get user by ID
     */
    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "USER"})
    public Response getUserById(
            @PathParam("id") @NotNull Long id,
            @HeaderParam("Accept-Language") @DefaultValue("en") String acceptLanguage) {
        
        logger.debug("Getting user by ID: {}, language: {}", id, acceptLanguage);
        
        User user = userService.findById(id);
        UserResponse response = convertToResponse(user);
        
        return Response.ok(response)
                .header("Content-Language", acceptLanguage)
                .build();
    }

    /**
     * Get user by username
     */
    @GET
    @Path("/username/{username}")
    @RolesAllowed("ADMIN")
    public Response getUserByUsername(
            @PathParam("username") String username) {
        
        logger.debug("Getting user by username: {}", username);
        
        return userService.findByUsername(username)
                .map(this::convertToResponse)
                .map(userResponse -> Response.ok(userResponse).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * Create new user
     */
    @POST
    @RolesAllowed("ADMIN")
    public Response createUser(
            @Valid UserCreateRequest request,
            @HeaderParam("X-Request-ID") String requestId) {
        
        logger.info("Creating new user: {}, requestId: {}", request.getUsername(), requestId);
        
        User user = convertFromCreateRequest(request);
        User createdUser = userService.createUser(user);
        UserResponse response = convertToResponse(createdUser);
        
        return Response.status(Response.Status.CREATED)
                .header("X-Request-ID", requestId)
                .entity(response)
                .build();
    }

    /**
     * Update existing user
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "USER"})
    public Response updateUser(
            @PathParam("id") @NotNull Long id,
            @Valid UserUpdateRequest request,
            @HeaderParam("If-Match") String ifMatch) {
        
        logger.info("Updating user: {}, ifMatch: {}", id, ifMatch);
        
        User user = convertFromUpdateRequest(request);
        user.setId(id);
        
        User updatedUser = userService.updateUser(user);
        UserResponse response = convertToResponse(updatedUser);
        
        return Response.ok()
                .header("ETag", "\"" + updatedUser.getUpdatedAt().toString() + "\"")
                .entity(response)
                .build();
    }

    /**
     * Update user status (activate/deactivate)
     */
    @PATCH
    @Path("/{id}/status")
    @RolesAllowed("ADMIN")
    public Response updateUserStatus(
            @PathParam("id") @NotNull Long id,
            @QueryParam("active") boolean active) {
        
        logger.info("Updating user status: {} to {}", id, active);
        
        User updatedUser = userService.updateUserStatus(id, active);
        UserResponse response = convertToResponse(updatedUser);
        
        return Response.ok(response).build();
    }

    /**
     * Delete user
     */
    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response deleteUser(
            @PathParam("id") @NotNull Long id,
            @HeaderParam("X-Confirm-Delete") @DefaultValue("false") boolean confirmDelete) {
        
        if (!confirmDelete) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        logger.info("Deleting user: {}", id);
        userService.deleteUser(id);
        
        return Response.noContent().build();
    }

    /**
     * Add role to user
     */
    @POST
    @Path("/{userId}/roles/{roleId}")
    @RolesAllowed("ADMIN")
    public Response addRoleToUser(
            @PathParam("userId") @NotNull Long userId,
            @PathParam("roleId") @NotNull Long roleId) {
        
        logger.info("Adding role {} to user {}", roleId, userId);
        
        User updatedUser = userService.addRoleToUser(userId, roleId);
        UserResponse response = convertToResponse(updatedUser);
        
        return Response.ok(response).build();
    }

    /**
     * Remove role from user
     */
    @DELETE
    @Path("/{userId}/roles/{roleId}")
    @RolesAllowed("ADMIN")
    public Response removeRoleFromUser(
            @PathParam("userId") @NotNull Long userId,
            @PathParam("roleId") @NotNull Long roleId) {
        
        logger.info("Removing role {} from user {}", roleId, userId);
        userService.removeRoleFromUser(userId, roleId);
        
        return Response.noContent().build();
    }

    /**
     * Get active users
     */
    @GET
    @Path("/active")
    @RolesAllowed("ADMIN")
    public Response getActiveUsers() {
        logger.debug("Getting all active users");
        
        List<User> activeUsers = userService.findActiveUsers();
        List<UserResponse> responses = activeUsers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return Response.ok(responses).build();
    }

    /**
     * Get users by role
     */
    @GET
    @Path("/role/{roleName}")
    @RolesAllowed("ADMIN")
    public Response getUsersByRole(
            @PathParam("roleName") String roleName) {
        
        logger.debug("Getting users by role: {}", roleName);
        
        List<User> users = userService.findUsersByRole(roleName);
        List<UserResponse> responses = users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return Response.ok(responses).build();
    }

    /**
     * Get user count
     */
    @GET
    @Path("/count")
    @RolesAllowed("ADMIN")
    public Response getUserCount(
            @QueryParam("activeOnly") @DefaultValue("true") boolean activeOnly) {
        
        long count = activeOnly ? userService.countActiveUsers() : userService.findAll(0, Integer.MAX_VALUE).size();
        return Response.ok(count).build();
    }

    /**
     * Send welcome email (async operation)
     */
    @POST
    @Path("/{id}/welcome-email")
    @RolesAllowed("ADMIN")
    public Response sendWelcomeEmail(@PathParam("id") @NotNull Long id) {
        logger.info("Triggering welcome email for user: {}", id);
        userService.sendWelcomeEmailAsync(id);
        return Response.status(Response.Status.ACCEPTED).build();
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

