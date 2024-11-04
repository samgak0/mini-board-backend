package shop.samgak.mini_board.unit;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.post.controllers.PostController;
import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.post.services.PostService;
import shop.samgak.mini_board.security.WithMockMyUserDetails;
import shop.samgak.mini_board.user.dto.UserDTO;

@ActiveProfiles("test")
@WebMvcTest(controllers = { PostController.class })
@AutoConfigureMockMvc(addFilters = false)
public class PostControllerUnitTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private PostService postService;

        @BeforeEach
        public void setUp() {
        }

        @AfterEach
        public void cleanUp() {
        }

        @Test
        public void testGetTop10Posts() throws Exception {
                List<PostDTO> mockPosts = new ArrayList<>();
                mockPosts.add(new PostDTO(1L, null, "First Post", "Content of the first post", 0L, Instant.now(),
                                Instant.now(), null,
                                null));
                mockPosts.add(new PostDTO(2L, null, "Second Post", "Content of the second post", 0L, Instant.now(),
                                Instant.now(), null,
                                null));

                when(postService.getTop10()).thenReturn(mockPosts);

                mockMvc.perform(get("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.length()").value(2))
                                .andExpect(jsonPath("$.data[0].title").value("First Post"))
                                .andExpect(jsonPath("$.data[1].title").value("Second Post"))
                                .andExpect(jsonPath("$.code").value("SUCCESS"));
        }

        @Test
        public void testGetPostById() throws Exception {
                Long postId = 1L;

                PostDTO mockPost = new PostDTO(postId, new UserDTO(1L, "user"), "First Post",
                                "Content of the first post", 0L, Instant.now(), Instant.now(), null, null);

                when(postService.getPostById(postId)).thenReturn(mockPost);

                mockMvc.perform(get("/api/posts/{id}", postId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.title").value("First Post"))
                                .andExpect(jsonPath("$.data.content").value("Content of the first post"))
                                .andExpect(jsonPath("$.code").value("SUCCESS"));
        }

        @Test
        public void testGetPostByIdNotFound() throws Exception {
                Long postId = 1L;

                when(postService.getPostById(postId))
                                .thenThrow(new ResourceNotFoundException("Post not found with id: " + postId));

                mockMvc.perform(get("/api/posts/{id}", postId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Post not found with id: " + postId))
                                .andExpect(jsonPath("$.code").value("FAILURE"));
        }

        @Test
        public void createPostUserNotFound() throws Exception {
                String title = "Test Title";
                String content = "Test Content";

                mockMvc.perform(post("/api/posts")
                                .param("title", title)
                                .param("content", content))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockMyUserDetails
        public void createPostMissingTitle() throws Exception {
                String content = "Test Content";

                mockMvc.perform(post("/api/posts")
                                .param("content", content))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockMyUserDetails
        public void createPostMissingContent() throws Exception {
                String title = "Test Title";

                mockMvc.perform(post("/api/posts")
                                .param("title", title))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockMyUserDetails
        public void createPostMissingTitleAndContent() throws Exception {
                mockMvc.perform(post("/api/posts"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockMyUserDetails
        public void createPostSuccess() throws Exception {
                String title = "Test Title";
                String content = "Test Content";

                mockMvc.perform(post("/api/posts")
                                .param("title", title)
                                .param("content", content))
                                .andExpect(status().isCreated());
        }

        @Test
        @WithMockMyUserDetails
        public void updatePostSuccess() throws Exception {
                Long postId = 1L;
                String title = "Updated Title";
                String content = "Updated Content";

                mockMvc.perform(put("/api/posts/{id}", postId)
                                .param("title", title)
                                .param("content", content))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value("SUCCESS"));
        }

        @Test
        public void updatePostUnauthorized() throws Exception {
                Long postId = 1L;
                String title = "Updated Title";
                String content = "Updated Content";

                mockMvc.perform(put("/api/posts/{id}", postId)
                                .param("title", title)
                                .param("content", content))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockMyUserDetails
        public void deletePostSuccess() throws Exception {
                Long postId = 1L;

                mockMvc.perform(delete("/api/posts/{id}", postId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value("SUCCESS"));
        }

        @Test
        public void deletePostUnauthorized() throws Exception {
                Long postId = 1L;

                mockMvc.perform(delete("/api/posts/{id}", postId))
                                .andExpect(status().isUnauthorized());
        }
}
