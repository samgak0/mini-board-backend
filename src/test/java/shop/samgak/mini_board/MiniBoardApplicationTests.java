package shop.samgak.mini_board;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import shop.samgak.mini_board.user.controllers.UserController;
import shop.samgak.mini_board.user.services.UserService;

/**
 * Integration tests for the MiniBoard application.
 * This class tests user authentication and access to protected resources
 * using the TestRestTemplate provided by Spring Boot.
 *
 * The TestRestTemplate allows for making HTTP requests to the application
 * during testing, simulating interactions as if they were coming from a client.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class MiniBoardApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private UserService userService;

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

        String sessionCookie = loginResponse.getHeaders().getFirst(SET_COOKIE);

        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.add("Cookie", sessionCookie);

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

    /**
     * Tests user registration when the email is already in use.
     * 
     * @throws Exception if any error occurs during the request
     */
    @Test
    public void testRegisterEmailAlreadyUsed() throws Exception {
        String username = "newUser";
        String email = "existing@example.com";
        String password = "password123";

        when(userService.existEmail(email)).thenReturn(true);
        when(userService.existUsername(username)).thenReturn(false);

        MultiValueMap<String, String> registerRequest = new LinkedMultiValueMap<>();
        registerRequest.add("username", username);
        registerRequest.add("email", email);
        registerRequest.add("password", password);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<String> registerResponse = restTemplate.postForEntity("/api/users/register", requestEntity,
                String.class);

        assertThat(registerResponse.getStatusCode()).isEqualTo(CONFLICT);
        assertThat(registerResponse.getBody()).contains(UserController.ERROR_EMAIL_ALREADY_USED);
    }

    /**
     * Tests user registration when the username is already in use.
     * 
     * @throws Exception if any error occurs during the request
     */
    @Test
    public void testRegisterUsernameAlreadyUsed() throws Exception {
        String username = "existingUser";
        String email = "newuser@example.com";
        String password = "password123";

        when(userService.existUsername(username)).thenReturn(true);
        when(userService.existEmail(email)).thenReturn(false);

        MultiValueMap<String, String> registerRequest = new LinkedMultiValueMap<>();
        registerRequest.add("username", username);
        registerRequest.add("email", email);
        registerRequest.add("password", password);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<String> registerResponse = restTemplate.postForEntity("/api/users/register", requestEntity,
                String.class);

        assertThat(registerResponse.getStatusCode()).isEqualTo(CONFLICT);
        assertThat(registerResponse.getBody()).contains(UserController.ERROR_USERNAME_ALREADY_USED);
    }

    /**
     * Tests user registration when username is missing.
     */
    @Test
    public void testRegisterMissingUsername() throws Exception {
        String email = "newuser@example.com";
        String password = "password123";

        MultiValueMap<String, String> registerRequest = new LinkedMultiValueMap<>();
        registerRequest.add("email", email);
        registerRequest.add("password", password);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<String> registerResponse = restTemplate.postForEntity("/api/users/register", requestEntity,
                String.class);

        assertThat(registerResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    /**
     * Tests user registration when password is missing.
     */
    @Test
    public void testRegisterMissingPassword() throws Exception {
        String username = "newUser";
        String email = "newuser@example.com";

        MultiValueMap<String, String> registerRequest = new LinkedMultiValueMap<>();
        registerRequest.add("username", username);
        registerRequest.add("email", email);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<String> registerResponse = restTemplate.postForEntity("/api/users/register", requestEntity,
                String.class);

        assertThat(registerResponse.getStatusCode()).isEqualTo(BAD_REQUEST);
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
        assertThat(postResponse.getStatusCode()).isEqualTo(CREATED);
    }

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
}
