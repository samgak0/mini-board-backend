package shop.samgak.mini_board.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import shop.samgak.mini_board.exceptions.GlobalExceptionHandler;
import shop.samgak.mini_board.exceptions.MessageProvider;
import shop.samgak.mini_board.post.controllers.PostController;
import shop.samgak.mini_board.post.services.PostService;
import shop.samgak.mini_board.security.MyUserDetails;
import shop.samgak.mini_board.user.controllers.UserController;
import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.user.services.UserService;
import shop.samgak.mini_board.utility.ApiResponse;

/**
 * Unit tests for the UserController class.
 * This class tests the API endpoints related to user operations,
 * including username checks, email checks, registration,
 * and password management.
 */
public class UserControllerUnitTest {

        // Constants for API paths, parameters, and JSON fields
        private static final String API_USERS_CHECK_USERNAME = "/api/users/check/username";
        private static final String API_USERS_CHECK_EMAIL = "/api/users/check/email";
        private static final String API_USERS_REGISTER = "/api/users/register";
        private static final String API_USERS_PASSWORD = "/api/users/password";
        private static final String API_USERS_STATUS = "/api/users/check/status";

        private static final String PARAM_USERNAME = "username";
        private static final String PARAM_EMAIL = "email";
        private static final String PARAM_PASSWORD = "password";

        private static final String JSON_PATH_MESSAGE = "$.message";
        private static final String JSON_PATH_CODE = "$.code";
        private static final String JSON_PATH_DATA = "$.data";

        private MockMvc mockMvc;

        @Mock
        private UserService userService;

        @Mock
        private PostService postService;

        @Mock
        private MockHttpSession session;

        @InjectMocks
        private UserController userController;

        /**
         * Sets up the MockMvc instance and initializes mocks before each test.
         */
        @BeforeEach
        public void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders
                                .standaloneSetup(userController, new PostController(postService))
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();
                SecurityContextHolder.clearContext();
        }

        // General operation tests

        /**
         * Tests the success scenario for checking username availability.
         */
        @Test
        public void testCheckUsernameSuccess() throws Exception {
                String username = "testUser";

                when(userService.existUsername(username)).thenReturn(false);

                mockMvc.perform(post(API_USERS_CHECK_USERNAME)
                                .param(PARAM_USERNAME, username)
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE).value(UserController.MESSAGE_USERNAME_AVAILABLE))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.SUCCESS.toString()));

                verify(session).setAttribute(UserController.SESSION_CHECKED_USER, username);
        }

        /**
         * Tests the success scenario for checking email availability.
         */
        @Test
        public void testCheckEmailSuccess() throws Exception {
                String email = "test@example.com";

                when(userService.existEmail(email)).thenReturn(false);

                mockMvc.perform(post(API_USERS_CHECK_EMAIL)
                                .param(PARAM_EMAIL, email)
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE).value(UserController.MESSAGE_EMAIL_AVAILABLE))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.SUCCESS.toString()));

                verify(session).setAttribute(UserController.SESSION_CHECKED_EMAIL, email);
        }

        /**
         * Tests the success scenario for registering a new user.
         */
        @Test
        public void testRegisterSuccess() throws Exception {
                String username = "newUser";
                String email = "newuser@example.com";
                String password = "password123";
                Long userId = 1L;

                when(userService.save(username, email, password)).thenReturn(userId);

                mockMvc.perform(post(API_USERS_REGISTER)
                                .param(PARAM_USERNAME, username)
                                .param(PARAM_EMAIL, email)
                                .param(PARAM_PASSWORD, password)
                                .sessionAttr(UserController.SESSION_CHECKED_USER, username)
                                .sessionAttr(UserController.SESSION_CHECKED_EMAIL, email)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isCreated())
                                .andExpect(header().string("Location", "/api/users/1/info"))
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value(UserController.MESSAGE_REGISTER_SUCCESSFUL))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.SUCCESS.toString()));
        }

        /**
         * Tests the success scenario for checking password validity.
         */
        @Test
        public void testCheckPasswordSuccess() throws Exception {
                // Valid passwords for testing
                String[] validPasswords = {
                                "ValidPassword1!",
                                "AnotherValid1$",
                                "StrongPass2@",
                                "ThisIsValid3#",
                                "Password4!"
                };

                // Test for valid passwords
                for (String validPassword : validPasswords) {
                        mockMvc.perform(post("/api/users/check/password")
                                        .param("password", validPassword)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath(JSON_PATH_DATA).value(true))
                                        .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.SUCCESS.toString()));
                }
        }

        /**
         * Tests the failure scenario for checking password validity.
         */
        @Test
        public void testCheckPasswordFailure() throws Exception {
                // Invalid passwords for testing
                String[] invalidPasswords = {
                                "short", // Too short
                                "nouppercase123!", // No uppercase letters
                                "NOLOWERCASE123!", // No lowercase letters
                                "NoSpecialChars1", // No special characters
                                "noNumbers!", // No numbers
                                "ALLUPPERCASE!", // Only uppercase
                                "alllowercase!" // Only lowercase
                };

                // Test for invalid passwords
                for (String invalidPassword : invalidPasswords) {
                        mockMvc.perform(post("/api/users/check/password")
                                        .param("password", invalidPassword)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath(JSON_PATH_DATA).value(false))
                                        .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.SUCCESS.toString()));
                }
        }

        // Exception handling tests

        /**
         * Tests registering a user when the email is already used.
         */
        @Test
        public void testRegisterEmailAlreadyUsedFailure() throws Exception {
                String username = "newUser";
                String email = "usedemail@example.com";
                String password = "password123";

                when(userService.existEmail(email)).thenReturn(true);

                mockMvc.perform(post(API_USERS_REGISTER)
                                .param(PARAM_USERNAME, username)
                                .param(PARAM_EMAIL, email)
                                .param(PARAM_PASSWORD, password)
                                .sessionAttr(UserController.SESSION_CHECKED_USER, username)
                                .sessionAttr(UserController.SESSION_CHECKED_EMAIL, email)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE).value(UserController.ERROR_EMAIL_ALREADY_USED))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.USED.toString()));
        }

        /**
         * Tests checking username when the username is missing.
         */
        @Test
        public void testCheckUsernameMissingFailure() throws Exception {
                mockMvc.perform(post(API_USERS_CHECK_USERNAME)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value(MessageProvider.getMissingParameterMessage(PARAM_USERNAME)))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.FAILURE.toString()));
        }

        /**
         * Tests email check when the email is missing.
         */
        @Test
        public void testCheckEmailMissingFailure() throws Exception {
                mockMvc.perform(post(API_USERS_CHECK_EMAIL)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value(MessageProvider.getMissingParameterMessage(PARAM_EMAIL)))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.FAILURE.toString()));
        }

        /**
         * Tests user registration with missing username.
         */
        @Test
        public void testRegisterMissingUsernameFailure() throws Exception {
                String email = "newuser@example.com";
                String password = "password123";

                mockMvc.perform(post(API_USERS_REGISTER)
                                .param(PARAM_EMAIL, email)
                                .param(PARAM_PASSWORD, password)
                                .sessionAttr(UserController.SESSION_CHECKED_EMAIL, email)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value(MessageProvider.getMissingParameterMessage(PARAM_USERNAME)))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.FAILURE.toString()));
        }

        /**
         * Tests changing the password with a valid password.
         */
        @Test
        public void testChangePasswordSuccess() throws Exception {
                String username = "newUser";
                String email = "newuser@example.com";
                String validPassword = "ValidPassword1!";

                setSecurityContext();

                mockMvc.perform(put(API_USERS_PASSWORD)
                                .param(PARAM_PASSWORD, validPassword)
                                .contentType(MediaType.APPLICATION_JSON)
                                .sessionAttr(UserController.SESSION_CHECKED_USER, username)
                                .sessionAttr(UserController.SESSION_CHECKED_EMAIL, email))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value(UserController.MESSAGE_PASSWORD_CHANGE_SUCCESSFUL))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.SUCCESS.toString()));
        }

        /**
         * Tests changing the password with an invalid password.
         */
        @Test
        public void testChangePasswordInvalidFailure() throws Exception {
                String invalidPassword = "short";

                setSecurityContext();

                mockMvc.perform(put(API_USERS_PASSWORD)
                                .param(PARAM_PASSWORD, invalidPassword)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value(UserController.ERROR_INVALID_PASSWORD_FORMAT))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.FAILURE.toString()));
        }

        /**
         * Tests password change when the password is missing.
         */
        @Test
        public void testChangePasswordMissingFailure() throws Exception {
                mockMvc.perform(put(API_USERS_PASSWORD)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value(MessageProvider.getMissingParameterMessage(PARAM_PASSWORD)))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.FAILURE.toString()));
        }

        /**
         * Tests checkLoginStatus when the user is logged in.
         */
        @Test
        public void testCheckLoginStatusLoggedInSuccess() throws Exception {
                setSecurityContext();

                mockMvc.perform(get(API_USERS_STATUS)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE).value(UserController.MESSAGE_LOGIN_STATUS))
                                .andExpect(jsonPath(JSON_PATH_DATA).value(true))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.SUCCESS.toString()));
        }

        /**
         * Tests checkLoginStatus when the user is not logged in.
         */
        @Test
        public void testCheckLoginStatusNotLoggedInSuccess() throws Exception {

                mockMvc.perform(get(API_USERS_STATUS)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andDo(MockMvcResultHandlers.print())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE).value(UserController.MESSAGE_LOGIN_STATUS))
                                .andExpect(jsonPath(JSON_PATH_DATA).value(false))
                                .andExpect(jsonPath(JSON_PATH_CODE).value(ApiResponse.Code.SUCCESS.toString()));
        }

        @Test
        public void testMeSuccess() throws Exception {
                setSecurityContext();

                mockMvc.perform(MockMvcRequestBuilders.get("/api/users/me"))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Login status"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1L))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("user"));
        }

        private void setSecurityContext() {
                UserDTO mockUserDTO = new UserDTO(1L, "user");
                MyUserDetails myUserDetails = new MyUserDetails(mockUserDTO, null);

                Authentication authentication = mock(Authentication.class);
                SecurityContext securityContext = mock(SecurityContext.class);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.getPrincipal()).thenReturn(myUserDetails);
                SecurityContextHolder.setContext(securityContext);
        }

        @Test
        public void testMeUnauthorized() throws Exception {
                Authentication authentication = mock(Authentication.class);
                SecurityContext securityContext = mock(SecurityContext.class);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.getPrincipal()).thenReturn(null);
                SecurityContextHolder.setContext(securityContext);

                mockMvc.perform(MockMvcRequestBuilders.get("/api/users/me")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                                .value("Authentication is required"));
        }

        @Test
        public void testMeUnexpectedDetailsType() throws Exception {
                Authentication authentication = mock(Authentication.class);
                when(authentication.getDetails()).thenReturn(new Object());
                SecurityContext securityContext = mock(SecurityContext.class);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                SecurityContextHolder.setContext(securityContext);

                mockMvc.perform(MockMvcRequestBuilders.get("/api/users/me")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                                                .value("Authentication is required"));
        }
}