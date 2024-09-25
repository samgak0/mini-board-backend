package shop.samgak.mini_board;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import shop.samgak.mini_board.config.GlobalExceptionHandler;
import shop.samgak.mini_board.post.controllers.PostController;
import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.post.services.PostService;
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
@Tag("unit")
public class UserControllerTest {

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
        }

        /**
         * Tests successful username availability check.
         * This method simulates a request to check if a username is available
         * and verifies the response.
         *
         * @throws Exception if any error occurs during the request
         */
        @Test
        public void testCheckUsernameSuccess() throws Exception {
                String username = "testUser";

                when(userService.existUsername(username)).thenReturn(false);

                mockMvc.perform(post("/api/users/check/username")
                                .param("username", username)
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Username is available"))
                                .andExpect(jsonPath("$.code").value(ApiResponse.Code.SUCCESS.toString()));

                verify(session).setAttribute("checked_user_name", username);
        }

        /**
         * Tests retrieval of all posts for an authenticated user.
         * This method simulates a request to get posts and verifies the response.
         *
         * @throws Exception if any error occurs during the request
         */
        @Test
        public void testGetPostsAuthenticated() throws Exception {
                UserDetails mockUser = mock(UserDetails.class);
                when(userService.getCurrentUser()).thenReturn(Optional.of(mockUser));
                when(mockUser.getUsername()).thenReturn("authenticatedUser");

                UserDTO mockUserDTO = new UserDTO();
                mockUserDTO.setId(1L);
                mockUserDTO.setUsername("authenticatedUser");

                List<PostDTO> mockPosts = new ArrayList<>();
                mockPosts.add(new PostDTO(1L, mockUserDTO, "First Post", "Content of the first post",
                                LocalDateTime.now(),
                                LocalDateTime.now()));
                mockPosts.add(new PostDTO(2L, mockUserDTO, "Second Post", "Content of the second post",
                                LocalDateTime.now(),
                                LocalDateTime.now()));

                when(postService.getAll()).thenReturn(mockPosts);

                mockMvc.perform(get("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].title").value("First Post"))
                                .andExpect(jsonPath("$[1].title").value("Second Post"));
        }

        /**
         * Tests username availability check when the username is already used.
         * This method simulates a request to check a username that is in use
         * and verifies the response.
         *
         * @throws Exception if any error occurs during the request
         */
        @Test
        public void testCheckUsernameAlreadyUsed() throws Exception {
                String username = "existingUser";

                when(userService.existUsername(username)).thenReturn(true);

                mockMvc.perform(post("/api/users/check/username")
                                .param("username", username)
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.message").value("Username already used"))
                                .andExpect(jsonPath("$.code").value(ApiResponse.Code.USED.toString()));

                verify(session, never()).setAttribute("checked_user_name", username);
        }

        /**
         * Tests successful email availability check.
         * This method simulates a request to check if an email is available
         * and verifies the response.
         *
         * @throws Exception if any error occurs during the request
         */
        @Test
        public void testCheckEmailSuccess() throws Exception {
                String email = "test@example.com";

                when(userService.existEmail(email)).thenReturn(false);

                mockMvc.perform(post("/api/users/check/email")
                                .param("email", email)
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Email is available"))
                                .andExpect(jsonPath("$.code").value(ApiResponse.Code.SUCCESS.toString()));

                verify(session).setAttribute("checked_email", email);
        }

        /**
         * Tests successful user registration.
         * This method simulates a request to register a new user
         * and verifies the response.
         *
         * @throws Exception if any error occurs during the request
         */
        @Test
        public void testRegisterSuccess() throws Exception {
                String username = "newUser";
                String email = "newuser@example.com";
                String password = "password123";
                Long userId = 1L;

                when(userService.save(username, email, password)).thenReturn(userId);

                mockMvc.perform(post("/api/users/register")
                                .param("username", username)
                                .param("email", email)
                                .param("password", password)
                                .sessionAttr("checked_user_name", username)
                                .sessionAttr("checked_email", email)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isCreated())
                                .andExpect(header().string("Location", "/api/users/1/info"))
                                .andExpect(jsonPath("$.message").value("Register successful"))
                                .andExpect(jsonPath("$.code").value(ApiResponse.Code.SUCCESS.toString()));
        }

        /**
         * Tests unauthorized password change attempt.
         * This method simulates a request to change the password
         * when the user is not authenticated.
         *
         * @throws Exception if any error occurs during the request
         */
        @Test
        public void testChangePasswordUnauthorized() throws Exception {
                when(userService.getCurrentUser()).thenReturn(Optional.empty());

                mockMvc.perform(put("/api/users/password")
                                .param("password", "newPassword123")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.message").value("Authentication required"));
        }

        /**
         * Tests successful password change.
         * This method simulates a request to change the password
         * for an authenticated user and verifies the response.
         *
         * @throws Exception if any error occurs during the request
         */
        @Test
        public void testChangePasswordSuccess() throws Exception {
                String username = "testUser";
                String newPassword = "newPassword123";

                UserDetails mockUser = mock(UserDetails.class);
                when(userService.getCurrentUser()).thenReturn(Optional.of(mockUser));
                when(mockUser.getUsername()).thenReturn(username);

                mockMvc.perform(put("/api/users/password")
                                .param("password", newPassword)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Password Change successful"))
                                .andExpect(jsonPath("$.code").value(ApiResponse.Code.SUCCESS.toString()));

                verify(userService).changePassword(username, newPassword);
        }

        /**
         * Tests password change failure.
         * This method simulates a request to change the password
         * and verifies the appropriate error response when it fails.
         *
         * @throws Exception if any error occurs during the request
         */
        @Test
        public void testChangePasswordFailure() throws Exception {
                String username = "testUser";
                String newPassword = "newPassword123";

                UserDetails mockUser = mock(UserDetails.class);
                when(userService.getCurrentUser()).thenReturn(Optional.of(mockUser));
                when(mockUser.getUsername()).thenReturn(username);

                doThrow(new RuntimeException("Password change failed")).when(userService).changePassword(username,
                                newPassword);

                mockMvc.perform(put("/api/users/password")
                                .param("password", newPassword)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Password change failed"));
        }
}
