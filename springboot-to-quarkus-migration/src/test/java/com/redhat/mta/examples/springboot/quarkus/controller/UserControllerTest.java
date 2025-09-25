package com.redhat.mta.examples.springboot.quarkus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.mta.examples.springboot.quarkus.dto.UserCreateRequest;
import com.redhat.mta.examples.springboot.quarkus.model.User;
import com.redhat.mta.examples.springboot.quarkus.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * User Controller Test demonstrating Spring Boot testing patterns for Quarkus migration.
 * 
 * Migration to Quarkus:
 * - Replace @WebMvcTest with @QuarkusTest
 * - Replace @MockBean with @InjectMock
 * - Replace MockMvc with RestAssured
 * - Replace @WithMockUser with Quarkus security test utilities
 * - Replace Spring security test annotations with Quarkus equivalents
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User testUser;
    private UserCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        createRequest = new UserCreateRequest();
        createRequest.setUsername("newuser");
        createRequest.setEmail("new@example.com");
        createRequest.setFirstName("New");
        createRequest.setLastName("User");
        createRequest.setPassword("password123");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_WithAdminRole_ShouldReturnUsers() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 20), 1);
        when(userService.findAll(any())).thenReturn(userPage);

        // When & Then
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].username").value("testuser"))
                .andExpect(jsonPath("$.content[0].email").value("test@example.com"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_WithUserRole_ShouldReturnUsers() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 20), 1);
        when(userService.findAll(any())).thenReturn(userPage);

        // When & Then
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsers_WithoutAuthentication_ShouldReturn401() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_WithValidId_ShouldReturnUser() throws Exception {
        // Given
        when(userService.findById(1L)).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_WithValidData_ShouldCreateUser() throws Exception {
        // Given
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createUser_WithUserRole_ShouldReturn403() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_WithInvalidData_ShouldReturn400() throws Exception {
        // Given
        createRequest.setUsername(""); // Invalid username
        createRequest.setEmail("invalid-email"); // Invalid email

        // When & Then
        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void updateUser_OwnProfile_ShouldUpdateUser() throws Exception {
        // Given
        testUser.setId(1L);
        when(userService.updateUser(any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_WithAdminRole_ShouldDeleteUser() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/users/1")
                        .with(csrf())
                        .header("X-Confirm-Delete", "true"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteUser_WithUserRole_ShouldReturn403() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/users/1")
                        .with(csrf())
                        .header("X-Confirm-Delete", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getActiveUsers_WithAdminRole_ShouldReturnActiveUsers() throws Exception {
        // Given
        List<User> activeUsers = Arrays.asList(testUser);
        when(userService.findActiveUsers()).thenReturn(activeUsers);

        // When & Then
        mockMvc.perform(get("/api/users/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserCount_WithAdminRole_ShouldReturnCount() throws Exception {
        // Given
        when(userService.countActiveUsers()).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/api/users/count")
                        .param("activeOnly", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }
}
