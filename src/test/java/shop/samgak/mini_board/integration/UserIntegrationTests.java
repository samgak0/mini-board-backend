package shop.samgak.mini_board.integration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.boot.test.mock.mockito.MockBean;
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
        try {
            restTemplate.postForEntity("/api/users/register", requestEntity,
                    String.class);
            fail("Expected ResourceAccessException to be thrown");
        } catch (ResourceAccessException e) {
            assertThat(e).isInstanceOf(ResourceAccessException.class);
        }
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

        try {
            restTemplate.postForEntity("/api/users/register", requestEntity, String.class);
        } catch (ResourceAccessException e) {
            assertThat(e).isInstanceOf(ResourceAccessException.class);
        }
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
        try {
            restTemplate.postForEntity("/api/users/register", requestEntity, String.class);
        } catch (ResourceAccessException e) {
            assertThat(e).isInstanceOf(ResourceAccessException.class);
        }
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
        try {
            restTemplate.postForEntity("/api/users/register", requestEntity,
                    String.class);
        } catch (ResourceAccessException e) {
            assertThat(e).isInstanceOf(ResourceAccessException.class);
        }
    }

    @Test
    public void testMeUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            restTemplate.exchange("/api/users/me", HttpMethod.GET, request, String.class);
        } catch (ResourceAccessException e) {
            assertThat(e).isInstanceOf(ResourceAccessException.class);
        }
    }

    @Test
    public void testMeAuthorizedAfterLogin() {

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        String loginRequestBody = "{\"username\":\"user\", \"password\":\"password\"}";
        HttpEntity<String> loginRequest = new HttpEntity<>(loginRequestBody, loginHeaders);

        ResponseEntity<String> loginResponse = restTemplate.exchange("/api/auth/login", HttpMethod.POST, loginRequest,
                String.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        HttpHeaders loginResponseHeaders = loginResponse.getHeaders();
        List<String> cookies = loginResponseHeaders.get(HttpHeaders.SET_COOKIE);
        assertThat(cookies).isNotEmpty();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.put(HttpHeaders.COOKIE, cookies);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("/api/users/me", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"message\":\"Login status\"");
        assertThat(response.getBody()).contains("\"code\":\"SUCCESS\"");
        assertThat(response.getBody()).contains("\"data\":{\"id\":1,\"username\":\"user\"}}");
    }
}
