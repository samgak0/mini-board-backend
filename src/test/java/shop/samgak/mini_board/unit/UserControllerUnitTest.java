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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.samgak.mini_board.security.WithMockMyUserDetails;
import shop.samgak.mini_board.user.controllers.UserController;
import shop.samgak.mini_board.user.controllers.UserController.RegisterRequest;
import shop.samgak.mini_board.user.services.UserService;

@ActiveProfiles("test")
@WebMvcTest(controllers = { UserController.class })
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerUnitTest {

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

        @BeforeEach
        public void setUp() {
        }

        @AfterEach
        public void cleanUp() {
        }

        @Test
        public void testCheckUsernameSuccess() throws Exception {
                String username = "testUser";

                when(userService.existUsername(username)).thenReturn(false);

                MvcResult result = mockMvc.perform(post(API_USERS_CHECK_USERNAME)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("username", username))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value("Username is available"))
                                .andExpect(jsonPath(JSON_PATH_CODE)
                                                .value("SUCCESS"))
                                .andReturn();

                MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
                if (session == null) {
                        fail("session is null");
                } else {
                        assertThat(session.getAttribute(UserController.SESSION_CHECKED_USER))
                                        .isEqualTo(username);
                }
        }

        @Test
        public void testCheckEmailSuccess() throws Exception {
                String email = "test@example.com";

                when(userService.existEmail(email)).thenReturn(false);

                MvcResult result = mockMvc.perform(post(API_USERS_CHECK_EMAIL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("email", email))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value("Email is available"))
                                .andExpect(jsonPath(JSON_PATH_CODE)
                                                .value("SUCCESS"))
                                .andReturn();

                MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
                if (session == null) {
                        fail("session is null");
                } else {
                        assertThat(
                                        session.getAttribute(UserController.SESSION_CHECKED_EMAIL))
                                        .isEqualTo(email);
                }
        }

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
                                                .value("Registration successful"))
                                .andExpect(jsonPath(JSON_PATH_CODE)
                                                .value("SUCCESS"));
        }

        @Test
        @WithMockMyUserDetails
        public void testChangePasswordSuccess() throws Exception {
                String validPassword = "ValidPassword1!";

                String requestBody = objectMapper.writeValueAsString(Map.of("password", validPassword));

                mockMvc.perform(put(API_USERS_PASSWORD)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value("Password change successful"))
                                .andExpect(jsonPath(JSON_PATH_CODE)
                                                .value("SUCCESS"));
        }

        @Test
        @WithMockMyUserDetails
        public void testChangePasswordInvalidFailure() throws Exception {
                String invalidPassword = "short";

                String requestBody = objectMapper.writeValueAsString(Map.of("password", invalidPassword));

                mockMvc.perform(put(API_USERS_PASSWORD)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value("password: Invalid password format;"))
                                .andExpect(jsonPath(JSON_PATH_CODE)
                                                .value("FAILURE"));
        }

        @Test
        @WithMockMyUserDetails
        public void testMeSuccess() throws Exception {
                mockMvc.perform(get(API_USERS_ME)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value("Login status"))
                                .andExpect(jsonPath(JSON_PATH_CODE)
                                                .value("SUCCESS"))
                                .andExpect(jsonPath(JSON_PATH_DATA).exists());
        }

        @Test
        public void testMeUnauthorized() throws Exception {
                mockMvc.perform(get(API_USERS_ME)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath(JSON_PATH_MESSAGE)
                                                .value("Authentication is required"))
                                .andExpect(jsonPath(JSON_PATH_CODE)
                                                .value("FAILURE"));
        }
}
