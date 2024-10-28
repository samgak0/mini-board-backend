package shop.samgak.mini_board.unit;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.samgak.mini_board.security.WithMockMyUserDetails;
import shop.samgak.mini_board.user.controllers.UserController;
import shop.samgak.mini_board.user.services.UserService;
import shop.samgak.mini_board.utility.ApiResponse;

/**
 * Unit tests for the UserController class.
 * This class tests the API endpoints related to user operations,
 * including username checks, email checks, registration,
 * and password management.
 */
@WebMvcTest(controllers = { UserController.class })
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerUnitTest {

        // Constants for API paths, parameters, and JSON fields
        private static final String API_USERS_CHECK_USERNAME = "/api/users/check/username";
        private static final String API_USERS_CHECK_EMAIL = "/api/users/check/email";
        private static final String API_USERS_REGISTER = "/api/users/register";
        private static final String API_USERS_PASSWORD = "/api/users/password";
        private static final String API_USERS_ME = "/api/users/me";

        private static final String JSON_PATH_MESSAGE = "$.message";
        private static final String JSON_PATH_CODE = "$.code";
        private static final String JSON_PATH_DATA = "$.data";

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @Autowired
        private ObjectMapper objectMapper;

        /**
         * Sets up the MockMvc instance and initializes mocks before each test.
         */
        @BeforeEach
        public void setUp() {
        }

        @AfterEach
        public void cleanUp() {
        }

        // General operation tests

        /**
         * Tests the success scenario for checking username availability.
         */
        @Test
        public void testCheckUsernameSuccess() throws Exception {
                String username = "testUser";

                when(userService.existUsername(username)).thenReturn(false);

                String requestBody = objectMapper.writeValueAsString(new UsernameRequest(username));

                MvcResult result = mockMvc.perform(post(API_USERS_CHECK_USERNAME)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE).value(UserController.MESSAGE_USERNAME_AVAILABLE))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.SUCCESS.toString()))
                                .andReturn();
                MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
                if (session == null) {
                        fail("session is null");
                } else {
                        assertThat(session.getAttribute(UserController.SESSION_CHECKED_USER)).isEqualTo(username);
                }
        }

        /**
         * Tests the success scenario for checking email availability.
         */
        @Test
        public void testCheckEmailSuccess() throws Exception {
                String email = "test@example.com";

                when(userService.existEmail(email)).thenReturn(false);

                String requestBody = objectMapper.writeValueAsString(new EmailRequest(email));

                MvcResult result = mockMvc.perform(post(API_USERS_CHECK_EMAIL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE).value(UserController.MESSAGE_EMAIL_AVAILABLE))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.SUCCESS.toString()))
                                .andReturn();
                MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
                if (session == null) {
                        fail("session is null");
                } else {
                        assertThat(session.getAttribute(UserController.SESSION_CHECKED_EMAIL)).isEqualTo(email);
                }
        }

        /**
         * Tests the success scenario for registering a new user.
         */
        @Test
        public void testRegisterSuccess() throws Exception {
                String username = "newUser";
                String email = "newuser@example.com";
                String password = "password123Q!";
                Long userId = 1L;

                when(userService.save(username, email, password)).thenReturn(userId);

                String requestBody = objectMapper.writeValueAsString(new RegisterRequest(username, email, password));

                MockHttpSession session = new MockHttpSession();
                session.setAttribute(UserController.SESSION_CHECKED_USER, username);
                session.setAttribute(UserController.SESSION_CHECKED_EMAIL, email);

                mockMvc.perform(post(API_USERS_REGISTER)
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(session)
                                .content(requestBody))
                                .andExpect(status().isCreated())
                                .andExpect(header().string("Location", "/api/users/1/info"))
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value(UserController.MESSAGE_REGISTER_SUCCESSFUL))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.SUCCESS.toString()));
        }

        /**
         * Tests changing the password with a valid password.
         */
        @Test
        @WithMockMyUserDetails
        public void testChangePasswordSuccess() throws Exception {
                String validPassword = "ValidPassword1!";

                String requestBody = objectMapper.writeValueAsString(new PasswordRequest(validPassword));

                mockMvc.perform(put(API_USERS_PASSWORD)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value(UserController.MESSAGE_PASSWORD_CHANGE_SUCCESSFUL))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.SUCCESS.toString()));
        }

        /**
         * Tests changing the password with an invalid password.
         */
        @Test
        @WithMockMyUserDetails
        public void testChangePasswordInvalidFailure() throws Exception {
                String invalidPassword = "short";

                String requestBody = objectMapper.writeValueAsString(new PasswordRequest(invalidPassword));

                mockMvc.perform(put(API_USERS_PASSWORD)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value("password: Invalid password format;"))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.FAILURE.toString()));
        }

        /**
         * Tests the success scenario for fetching the current user's information.
         */
        @Test
        @WithMockMyUserDetails
        public void testMeSuccess() throws Exception {
                mockMvc.perform(get(API_USERS_ME)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE).value(UserController.MESSAGE_LOGIN_STATUS))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.SUCCESS.toString()))
                                .andExpect(jsonPath(JSON_PATH_DATA).exists());
        }

        /**
         * Tests the failure scenario for fetching the current user's information when
         * not logged in.
         */
        @Test
        public void testMeUnauthorized() throws Exception {
                mockMvc.perform(get(API_USERS_ME)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE).value("Authentication is required"))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.FAILURE.toString()));
        }

        record UsernameRequest(String username) {
        }

        record EmailRequest(String email) {
        }

        record RegisterRequest(String username, String email, String password) {
        }

        record PasswordRequest(String password) {
        };
}
