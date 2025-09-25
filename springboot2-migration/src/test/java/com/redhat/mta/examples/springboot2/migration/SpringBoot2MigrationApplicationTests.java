package com.redhat.mta.examples.springboot2.migration;

import com.redhat.mta.examples.springboot2.migration.model.User;
import com.redhat.mta.examples.springboot2.migration.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests using deprecated Spring Boot 2 testing patterns.
 * 
 * Deprecated features:
 * - @RunWith(SpringRunner.class) - deprecated in favor of @ExtendWith(SpringExtension.class)
 * - @Test from JUnit 4 - should use JUnit 5 @Test
 * - TestRestTemplate field injection with @Autowired
 * - @LocalServerPort with field injection
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringBoot2MigrationApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void contextLoads() {
        assertThat(restTemplate).isNotNull();
        assertThat(userRepository).isNotNull();
    }

    @Test
    public void testGetAllUsers() {
        User[] users = this.restTemplate.getForObject("http://localhost:" + port + "/springboot2-migration/api/users", User[].class);
        assertThat(users).isNotNull();
        assertThat(users.length).isGreaterThan(0);
    }

    @Test
    public void testCreateUser() {
        User newUser = new User("Test User", "test.user@example.com");
        
        User createdUser = this.restTemplate.postForObject(
            "http://localhost:" + port + "/springboot2-migration/api/users", 
            newUser, 
            User.class
        );
        
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getName()).isEqualTo("Test User");
        assertThat(createdUser.getEmail()).isEqualTo("test.user@example.com");
    }
}
