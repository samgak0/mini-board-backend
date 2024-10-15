package shop.samgak.mini_board.integration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.http.HttpStatus.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
public class UserIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private UserService userService;

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
}
