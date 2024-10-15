package shop.samgak.mini_board.integration;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

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
public class PostIntegrationTests {

    private final String postsUrl = "/api/posts";
    private final String loginUrl = "/api/auth/login";

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Tests accessing posts as a logged-in user.
     */
    @Test
    public void testAccessPostsAsLoggedInUser() throws Exception {
        String sessionCookie = loginUser("user", "password");

        // Prepare to access posts
        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.add("Cookie", sessionCookie);

        ResponseEntity<String> postsResponse = restTemplate.exchange(postsUrl, HttpMethod.GET,
                new HttpEntity<>(postHeaders), String.class);
        assertThat(postsResponse.getStatusCode()).isEqualTo(OK);
    }

    /**
     * Tests accessing posts without logging in.
     */
    @Test
    public void testAccessPostsAsNotLoggedInUser() throws Exception {
        // Attempt to access the posts without authentication
        ResponseEntity<String> postsResponse = restTemplate.getForEntity(postsUrl, String.class);
        assertThat(postsResponse.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    /**
     * Tests creating a new post as a logged-in user.
     */
    @Test
    public void testCreatePostAsLoggedInUser() throws Exception {
        String sessionCookie = loginUser("user", "password");

        // Prepare post creation request
        MultiValueMap<String, String> postRequest = new LinkedMultiValueMap<>();
        postRequest.add("title", "New Post Title");
        postRequest.add("content", "Content of the new post");

        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.add("Cookie", sessionCookie);
        HttpEntity<MultiValueMap<String, String>> postEntity = new HttpEntity<>(postRequest, postHeaders);

        // Create the post
        ResponseEntity<String> postResponse = restTemplate.postForEntity(postsUrl, postEntity, String.class);

        assertThat(postResponse.getStatusCode()).isEqualTo(CREATED);
    }

    /**
     * Tests creating a new post without being logged in.
     */
    @Test
    public void testCreatePostAsNotLoggedInUser() throws Exception {
        // Prepare post creation request
        MultiValueMap<String, String> postRequest = new LinkedMultiValueMap<>();
        postRequest.add("title", "Unauthorized Post Title");
        postRequest.add("content", "Content of the unauthorized post");

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> postEntity = new HttpEntity<>(postRequest, headers);

        // Attempt to create the post without a session cookie
        try {
            restTemplate.postForEntity(postsUrl, postEntity, String.class);
            fail("Expected ResourceAccessException to be thrown");
        } catch (RestClientException e) {
            assertThat(e).isInstanceOf(ResourceAccessException.class);
        }
    }
  
    /**
     * Utility method to simulate user login and return the session cookie.
     */
    private String loginUser(String username, String password) throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, requestEntity, String.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        return loginResponse.getHeaders().getFirst(SET_COOKIE);
    }
}
