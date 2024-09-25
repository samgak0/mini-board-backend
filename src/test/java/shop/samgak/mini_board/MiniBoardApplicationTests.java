package shop.samgak.mini_board;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;

import org.junit.jupiter.api.Tag;
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
        MultiValueMap<String, String> loginRequest = new LinkedMultiValueMap<>();
        loginRequest.add("username", "user");
        loginRequest.add("password", "password");

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, requestEntity, String.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(OK);

        // Get the session cookie from the response
        String sessionCookie = loginResponse.getHeaders().getFirst(SET_COOKIE);

        // Prepare headers for accessing protected posts
        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.add("Cookie", sessionCookie);

        // Access protected posts using the session cookie
        ResponseEntity<String> postsResponse = restTemplate.exchange(postsUrl, HttpMethod.GET,
                new HttpEntity<>(postHeaders),
                String.class);
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
        MultiValueMap<String, String> loginRequest = new LinkedMultiValueMap<>();
        loginRequest.add("username", "user");
        loginRequest.add("password", "password");

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(loginRequest, headers);

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
        MultiValueMap<String, String> loginRequest = new LinkedMultiValueMap<>();
        loginRequest.add("username", "user");
        loginRequest.add("password", "wrongpassword");

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(loginRequest, headers);

        try {
            restTemplate.postForEntity(loginUrl, requestEntity, String.class);
            fail("Expected ResourceAccessException to be thrown");
        } catch (RestClientException e) {
            assertThat(e).isInstanceOf(ResourceAccessException.class);
        }
    }

}
