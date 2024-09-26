package shop.samgak.mini_board;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import shop.samgak.mini_board.config.GlobalExceptionHandler;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.post.controllers.PostController;
import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.post.services.PostService;
import shop.samgak.mini_board.user.entities.User;
import shop.samgak.mini_board.utility.UserSessionHelper;

/**
 * Unit tests for the PostController class.
 * This class tests the API endpoints related to posts, including retrieval of
 * all posts and individual posts by ID.
 */
public class PostControllerTest {

        private MockMvc mockMvc;

        @Mock
        private PostService postService;

        @InjectMocks
        private PostController postController;

        @Mock
        private UserSessionHelper userSessionHelper;

        /**
         * Sets up the MockMvc instance and initializes mocks before each test.
         */
        @BeforeEach
        public void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders.standaloneSetup(postController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();
        }

        /**
         * Tests the retrieval of all posts.
         * This method simulates a request to get all posts and verifies the response.
         *
         * @throws Exception if any error occurs during the request
         */
        @Test
        public void testGetAllPosts() throws Exception {
                List<PostDTO> mockPosts = new ArrayList<>();
                mockPosts.add(new PostDTO(1L, null, "First Post", "Content of the first post", null, null));
                mockPosts.add(new PostDTO(2L, null, "Second Post", "Content of the second post", null, null));

                when(postService.getAll()).thenReturn(mockPosts);

                mockMvc.perform(get("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.length()").value(2))
                                .andExpect(jsonPath("$.data[0].title").value("First Post"))
                                .andExpect(jsonPath("$.data[1].title").value("Second Post"))
                                .andExpect(jsonPath("$.code").value("SUCCESS"));
        }

        /**
         * Tests the retrieval of a single post by ID.
         * This method simulates a request to get a post by its ID and verifies the
         * response.
         *
         * @throws Exception if any error occurs during the request
         */
        @Test
        public void testGetPostById() throws Exception {
                Long postId = 1L;
                PostDTO mockPost = new PostDTO(postId, null, "First Post", "Content of the first post", null, null);

                when(postService.getPostById(postId)).thenReturn(mockPost);

                mockMvc.perform(get("/api/posts/{id}", postId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.title").value("First Post"))
                                .andExpect(jsonPath("$.data.content").value("Content of the first post"))
                                .andExpect(jsonPath("$.code").value("SUCCESS"));
        }

        /**
         * Tests the retrieval of a post by ID when the post is not found.
         * This method simulates a request for a non-existent post and verifies the
         * appropriate error response.
         *
         * @throws Exception if any error occurs during the request
         */
        @Test
        public void testGetPostByIdNotFound() throws Exception {
                Long postId = 1L;

                // Mocking the service to throw an exception when the post is not found
                when(postService.getPostById(postId))
                                .thenThrow(new ResourceNotFoundException("Post not found with id: " + postId));

                mockMvc.perform(get("/api/posts/{id}", postId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Post not found with id: " + postId))
                                .andExpect(jsonPath("$.code").value("FAILURE"));
        }

        @Test
        public void createPostSuccess() throws Exception {
                String title = "Test Title";
                String content = "Test Content";

                MockHttpSession localSession = new MockHttpSession();
                User mockUser = new User();
                mockUser.setId(1L);
                mockUser.setUsername("username");
                mockUser.setEmail("user@example.com");
                when(userSessionHelper.getCurrentUserFromSession(localSession)).thenReturn(Optional.of(mockUser));
                when(postService.create(title, content, mockUser)).thenReturn(1L);

                // Act & Assert
                mockMvc.perform(post("/api/posts")
                                .session(localSession)
                                .param("title", title)
                                .param("content", content))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.message").value("success"))
                                .andExpect(jsonPath("$.code")
                                                .value("SUCCESS"));
        }

        @Test
        public void createPostUserNotFound() throws Exception {
                String title = "Test Title";
                String content = "Test Content";
                MockHttpSession localSession = new MockHttpSession();

                when(userSessionHelper.getCurrentUserFromSession(localSession)).thenReturn(Optional.empty());

                // Act & Assert
                mockMvc.perform(post("/api/posts")
                                .session(localSession)
                                .param("title", title)
                                .param("content", content))
                                .andExpect(status().isInternalServerError());
        }

        /**
         * Tests creating a post with missing title while logged in.
         */
        @Test
        public void createPostMissingTitle() throws Exception {
                String content = "Test Content";

                // Create a new MockHttpSession
                MockHttpSession localSession = new MockHttpSession();
                User mockUser = new User();
                mockUser.setId(1L);
                mockUser.setUsername("username");
                mockUser.setEmail("user@example.com");
                when(userSessionHelper.getCurrentUserFromSession(localSession)).thenReturn(Optional.of(mockUser));

                // Act & Assert
                mockMvc.perform(post("/api/posts")
                                .session(localSession)
                                .param("content", content))
                                .andExpect(status().isBadRequest());
        }

        /**
         * Tests creating a post with missing content while logged in.
         */
        @Test
        public void createPostMissingContent() throws Exception {
                String title = "Test Title";

                // Create a new MockHttpSession
                MockHttpSession localSession = new MockHttpSession();
                User mockUser = new User();
                mockUser.setId(1L);
                mockUser.setUsername("username");
                mockUser.setEmail("user@example.com");
                when(userSessionHelper.getCurrentUserFromSession(localSession)).thenReturn(Optional.of(mockUser));

                // Act & Assert
                mockMvc.perform(post("/api/posts")
                                .session(localSession)
                                .param("title", title))
                                .andExpect(status().isBadRequest());
        }

        /**
         * Tests creating a post with both title and content missing while logged in.
         */
        @Test
        public void createPostMissingTitleAndContent() throws Exception {
                // Create a new MockHttpSession
                MockHttpSession localSession = new MockHttpSession();
                User mockUser = new User();
                mockUser.setId(1L);
                mockUser.setUsername("username");
                mockUser.setEmail("user@example.com");
                when(userSessionHelper.getCurrentUserFromSession(localSession)).thenReturn(Optional.of(mockUser));

                // Act & Assert
                mockMvc.perform(post("/api/posts")
                                .session(localSession))
                                .andExpect(status().isBadRequest());
        }
}
