package shop.samgak.mini_board;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * Integration tests for the MiniBoard application.
 * This class tests the user authentication and access to protected resources
 * using the TestRestTemplate provided by Spring Boot.
 *
 * The TestRestTemplate allows for making HTTP requests to the application
 * during testing, simulating interactions as if they were coming from a client.
 */
@Tag("integration")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class MiniBoardApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String loginUrl = "/api/users/login";
    private final String postsUrl = "/api/posts";

    /**
     * Tests successful login and access to protected posts.
     * This method simulates a user logging in and then accessing the posts API.
     *
     * @throws Exception if any error occurs during the request
     */
    @Test
    public void testLoginAndAccessPostsAsLoggedInUser() throws Exception {
        // Perform login
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "user");
        loginRequest.put("password", "password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, requestEntity, String.class);
        assertEquals(OK, loginResponse.getStatusCode());

        // Get the session cookie from the response
        String sessionCookie = loginResponse.getHeaders().getFirst(SET_COOKIE);

        // Prepare headers for accessing protected posts
        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.add("Cookie", sessionCookie);

        // Access protected posts using the session cookie
        ResponseEntity<String> postsResponse = restTemplate.exchange(postsUrl, HttpMethod.GET,
                new HttpEntity<>(postHeaders),
                String.class);
        assertEquals(OK, postsResponse.getStatusCode());
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
        assertEquals(UNAUTHORIZED, postsResponse.getStatusCode());
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
        headers.setContentType(APPLICATION_FORM_URLENCODED);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, requestEntity, String.class);
        assertEquals(OK, loginResponse.getStatusCode());
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
        headers.setContentType(APPLICATION_FORM_URLENCODED);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, requestEntity, String.class);
        assertEquals(UNAUTHORIZED, loginResponse.getStatusCode());
    }
}
