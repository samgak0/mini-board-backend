package shop.samgak.mini_board;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import shop.samgak.mini_board.config.GlobalExceptionHandler;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.post.controllers.PostController;
import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.post.services.PostService;

/**
 * Unit tests for the PostController class.
 * This class tests the API endpoints related to posts, including retrieval of
 * all posts and individual posts by ID.
 */
@Tag("unit")
public class PostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

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
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("First Post"))
                .andExpect(jsonPath("$[1].title").value("Second Post"));
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
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("First Post"))
                .andExpect(jsonPath("$.content").value("Content of the first post"));
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
                .andExpect(jsonPath("$.message").value("Post not found with id: " + postId));
    }
}
