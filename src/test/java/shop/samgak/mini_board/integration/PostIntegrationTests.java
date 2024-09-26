package shop.samgak.mini_board.integration;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

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
    private final String loginUrl = "/api/users/login";

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Tests accessing posts as a logged-in user.
     */
    @Test
    public void testAccessPostsAsLoggedInUser() throws Exception {
        // Simulate user login to get session cookie
        MultiValueMap<String, String> loginRequest = new LinkedMultiValueMap<>();
        loginRequest.add("username", "user");
        loginRequest.add("password", "password");

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> loginEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, loginEntity, String.class);
        String sessionCookie = loginResponse.getHeaders().getFirst(SET_COOKIE);

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
        // Simulate user login to get session cookie
        MultiValueMap<String, String> loginRequest = new LinkedMultiValueMap<>();
        loginRequest.add("username", "user");
        loginRequest.add("password", "password");

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> loginEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, loginEntity, String.class);
        String sessionCookie = loginResponse.getHeaders().getFirst(SET_COOKIE);

        // Prepare post creation request
        MultiValueMap<String, String> postRequest = new LinkedMultiValueMap<>();
        postRequest.add("title", "New Post Title");
        postRequest.add("content", "Content of the new post");

        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.add("Cookie", sessionCookie);
        HttpEntity<MultiValueMap<String, String>> postEntity = new HttpEntity<>(postRequest, postHeaders);

        // Create the post
        ResponseEntity<String> postResponse = restTemplate.postForEntity(postsUrl, postEntity, String.class);

        // Log the post creation response
        System.out.println("Post Creation Response Status: " + postResponse.getStatusCode());
        System.out.println("Post Creation Response Body: " + postResponse.getBody());

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
     * Tests creating a new post when both username and password are missing.
     */
    @Test
    public void testCreatePostWithMissingCredentialsBoth() throws Exception {
        // Simulate user login to get session cookie
        MultiValueMap<String, String> loginRequest = new LinkedMultiValueMap<>();
        loginRequest.add("username", "user");
        loginRequest.add("password", "password");

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> loginEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, loginEntity, String.class);
        String sessionCookie = loginResponse.getHeaders().getFirst(SET_COOKIE);

        // Prepare post creation request with missing username and password
        MultiValueMap<String, String> postRequest = new LinkedMultiValueMap<>();
        postRequest.add("title", "Unauthorized Post Title");
        postRequest.add("content", "Content of the unauthorized post");

        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.add("Cookie", sessionCookie);
        HttpEntity<MultiValueMap<String, String>> postEntity = new HttpEntity<>(postRequest, postHeaders);

        // Attempt to create the post without username and password
        postRequest.remove("title");
        postRequest.remove("content");

        ResponseEntity<String> postResponse = restTemplate.postForEntity(postsUrl, postEntity, String.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(BAD_REQUEST); // Assuming a 400 Bad Request
    }

    /**
     * Tests creating a new post when the username is missing.
     */
    @Test
    public void testCreatePostWithMissingUsername() throws Exception {
        // Simulate user login to get session cookie
        MultiValueMap<String, String> loginRequest = new LinkedMultiValueMap<>();
        loginRequest.add("username", "user");
        loginRequest.add("password", "password");

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> loginEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, loginEntity, String.class);
        String sessionCookie = loginResponse.getHeaders().getFirst(SET_COOKIE);

        // Prepare post creation request with missing username
        MultiValueMap<String, String> postRequest = new LinkedMultiValueMap<>();
        postRequest.add("title", "New Post Title");
        postRequest.add("content", "Content of the new post");

        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.add("Cookie", sessionCookie);
        HttpEntity<MultiValueMap<String, String>> postEntity = new HttpEntity<>(postRequest, postHeaders);

        // Remove username from the request
        postRequest.remove("username");

        ResponseEntity<String> postResponse = restTemplate.postForEntity(postsUrl, postEntity, String.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(BAD_REQUEST); // Assuming a 400 Bad Request
    }

    /**
     * Tests creating a new post when the password is missing.
     */
    @Test
    public void testCreatePostWithMissingPassword() throws Exception {
        // Simulate user login to get session cookie
        MultiValueMap<String, String> loginRequest = new LinkedMultiValueMap<>();
        loginRequest.add("username", "user");
        loginRequest.add("password", "password");

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> loginEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, loginEntity, String.class);
        String sessionCookie = loginResponse.getHeaders().getFirst(SET_COOKIE);

        // Prepare post creation request with missing password
        MultiValueMap<String, String> postRequest = new LinkedMultiValueMap<>();
        postRequest.add("title", "New Post Title");
        postRequest.add("content", "Content of the new post");

        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.add("Cookie", sessionCookie);
        HttpEntity<MultiValueMap<String, String>> postEntity = new HttpEntity<>(postRequest, postHeaders);

        // Remove password from the request
        postRequest.remove("password");

        ResponseEntity<String> postResponse = restTemplate.postForEntity(postsUrl, postEntity, String.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(BAD_REQUEST); // Assuming a 400 Bad Request
    }

    /**
     * Tests login failure when both username and password are missing.
     */
    @Test
    public void testLoginMissingBothAsNotLoggedInUser() throws Exception {
        MultiValueMap<String, String> loginRequest = new LinkedMultiValueMap<>();
        // Both username and password are omitted

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, requestEntity, String.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(BAD_REQUEST); // Assuming a 400 Bad Request
    }

    /**
     * Tests login failure when username is missing.
     */
    @Test
    public void testLoginMissingUsernameAsNotLoggedInUser() throws Exception {
        MultiValueMap<String, String> loginRequest = new LinkedMultiValueMap<>();
        loginRequest.add("password", "password"); // Only password is provided

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, requestEntity, String.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(BAD_REQUEST); // Assuming a 400 Bad Request
    }

    /**
     * Tests login failure when password is missing.
     */
    @Test
    public void testLoginMissingPasswordAsNotLoggedInUser() throws Exception {
        MultiValueMap<String, String> loginRequest = new LinkedMultiValueMap<>();
        loginRequest.add("username", "user"); // Only username is provided

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, requestEntity, String.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(BAD_REQUEST); // Assuming a 400 Bad Request
    }

}
