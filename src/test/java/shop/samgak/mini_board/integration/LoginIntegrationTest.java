package shop.samgak.mini_board.integration;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.http.HttpStatus.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration tests for the MiniBoard application.
 * This class tests user authentication and access to protected resources
 * using the TestRestTemplate provided by Spring Boot.
 *
 * The TestRestTemplate allows for making HTTP requests to the application
 * during testing, simulating interactions as if they were coming from a client.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class LoginIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String loginUrl = "/api/auth/login";

    /**
     * Tests successful login with valid credentials.
     * This method simulates a user logging in with correct username and password.
     *
     * @throws Exception if any error occurs during the request
     */
    @Test
    public void testLoginSuccess() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "user");
        loginRequest.put("password", "password");
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, requestEntity, String.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     * Tests login failure with invalid credentials.
     * This method simulates a user attempting to log in with an incorrect password.
     *
     * @throws Exception if any error occurs during the request
     */
    @Test
    public void testLoginFailure() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "user");
        loginRequest.put("password", "wrongpassword");
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForEntity(loginUrl, requestEntity, String.class);
            fail("Expected HttpClientErrorException to be thrown");
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(UNAUTHORIZED);
        }
    }

    /**
     * Tests login failure when both username and password are missing.
     */
    @Test
    public void testLoginMissingBoth() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForEntity(loginUrl, requestEntity, String.class);
            fail("Expected HttpClientErrorException to be thrown");
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(BAD_REQUEST);
        }
    }

    /**
     * Tests login failure when username is missing.
     */
    @Test
    public void testLoginMissingUsername() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("password", "password");
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForEntity(loginUrl, requestEntity, String.class);
            fail("Expected HttpClientErrorException to be thrown");
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(BAD_REQUEST);
        }
    }

    /**
     * Tests login failure when password is missing.
     */
    @Test
    public void testLoginMissingPassword() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "user");
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForEntity(loginUrl, requestEntity, String.class);
            fail("Expected HttpClientErrorException to be thrown");
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(BAD_REQUEST);
        }
    }

}