package shop.samgak.mini_board;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.http.HttpStatus.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Integration tests for the MiniBoard application.
 * This class tests the user authentication and access to protected resources
 * using the TestRestTemplate provided by Spring Boot.
 *
 * The @WithMockUser annotation allows for simulating authenticated users in the
 * tests, eliminating the need for actual login processes.
 */
@Tag("unit")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class MiniBoardApplicationUnitTests {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String loginUrl = "/api/users/login";
    private final String postsUrl = "/api/posts";

    /**
     * Tests access to protected posts as a logged-in user.
     * This method simulates a user accessing the posts API with mock user
     * authentication.
     *
     * @throws Exception if any error occurs during the request
     */
    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void testAccessPostsAsLoggedInUser() throws Exception {
        ResponseEntity<String> postsResponse = restTemplate.getForEntity(postsUrl, String.class);
        assertThat(postsResponse.getStatusCode()).isEqualTo(OK);
    }

    /**
     * Tests access to protected posts without authentication.
     * This method simulates an unauthorized user trying to access the posts API.
     *
     * @throws Exception if any error occurs during the request
     */
    @Test
    public void testAccessPostsAsNotLoggedInUser() throws Exception {
        ResponseEntity<String> postsResponse = restTemplate.getForEntity(postsUrl, String.class);
        assertThat(postsResponse.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, requestEntity, String.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(OK);
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, requestEntity, String.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }
}
