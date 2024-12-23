package shop.samgak.mini_board.unit;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import shop.samgak.mini_board.comment.controllers.CommentController;
import shop.samgak.mini_board.comment.dto.CommentDTO;
import shop.samgak.mini_board.comment.services.CommentService;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.security.WithMockMyUserDetails;
import shop.samgak.mini_board.user.dto.UserDTO;

@ActiveProfiles("test")
@WebMvcTest(controllers = { CommentController.class })
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerUnitTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CommentService commentService;

        @Autowired
        private ObjectMapper objectMapper;

        @BeforeEach
        public void setUp() {
        }

        @AfterEach
        public void cleanUp() {
        }

        @Test
        @WithMockMyUserDetails
        public void testGetComments() throws Exception {
                Long postId = 1L;

                List<CommentDTO> mockComments = new ArrayList<>();
                Long commentId1 = 1L;
                Long commentId2 = 2L;
                UserDTO user = new UserDTO(1L, "user");
                PostDTO post = new PostDTO(1L, user, "Post Title", "Post Content", 0L, Instant.now(), Instant.now(),
                                null, null);
                String content1 = "First Comment";
                String content2 = "Second Comment";
                CommentDTO comment1 = new CommentDTO(commentId1, user, post, null, content1, null, null);
                CommentDTO comment2 = new CommentDTO(commentId2, user, post, null, content2, null, null);

                mockComments.add(comment1);
                mockComments.add(comment2);
                when(commentService.get(1L)).thenReturn(mockComments);

                mockMvc.perform(get("/api/posts/{postId}/comments", postId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.data.length()").value(2))
                                .andExpect(jsonPath("$.data[0].content").value("First Comment"))
                                .andExpect(jsonPath("$.data[1].content").value("Second Comment"))
                                .andExpect(jsonPath("$.code").value("SUCCESS"))
                                .andExpect(jsonPath("$.message").value("success"));
        }

        @Test
        @WithMockMyUserDetails
        public void testGetCommentByIdNotFound() throws Exception {
                Long postId = 1L;

                when(commentService.get(postId))
                                .thenThrow(new ResourceNotFoundException(
                                                "Comment not found with Post ID : [" + postId + "]"));

                mockMvc.perform(get("/api/posts/{postId}/comments", postId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.code").value("FAILURE"))
                                .andExpect(jsonPath("$.message")
                                                .value("Comment not found with Post ID : [" + postId + "]"));
        }

        @Test
        public void createCommentUserNotFound() throws Exception {
                String content = "Test Content";
                Long postId = 1L;

                mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("content", content))))
                                .andExpect(status().isUnauthorized())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.code").value("FAILURE"))
                                .andExpect(jsonPath("$.message")
                                                .value("Authentication is required"));
        }

        @Test
        @WithMockMyUserDetails
        public void createCommentMissingContent() throws Exception {
                Long postId = 1L;

                mockMvc.perform(post("/api/posts/{postId}/comments", postId))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.code").value("FAILURE"))
                                .andExpect(jsonPath("$.message").value("Required request body is missing"));
        }

        @Test
        @WithMockMyUserDetails
        public void createCommentSuccess() throws Exception {
                String content = "Test Content";
                Long postId = 1L;
                Long userId = 1L;
                Long commentId = 1L;

                CommentDTO commentDTO = new CommentDTO();
                commentDTO.setId(commentId);
                commentDTO.setContent(content);
                commentDTO.setUser(new UserDTO(userId, "username"));
                commentDTO.setPost(
                                new PostDTO(2L, new UserDTO(userId, "username"), "Sample Title", "Sample Content", 0L,
                                                Instant.now(), Instant.now(), null, null));
                commentDTO.setCreatedAt(Instant.now());
                commentDTO.setUpdatedAt(Instant.now());

                when(commentService.create(content, postId, userId)).thenReturn(commentDTO);

                mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("content", content))))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.code").value("SUCCESS"))
                                .andExpect(jsonPath("$.message").value("Comment created successfully"));
        }

        @Test
        @WithMockMyUserDetails
        public void updateCommentSuccess() throws Exception {
                Long postId = 1L;
                Long commentId = 1L;
                String content = "Updated Content";

                doNothing().when(commentService).update(commentId, postId, content, 1L);

                mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", postId, commentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("content", content))))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.code").value("SUCCESS"))
                                .andExpect(jsonPath("$.message").value("Comment updated successfully"));
        }

        @Test
        public void updateCommentUnauthorized() throws Exception {
                Long postId = 1L;
                Long commentId = 1L;
                String content = "Updated Content";

                mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", postId, commentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("content", content))))
                                .andExpect(status().isUnauthorized())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.code").value("FAILURE"))
                                .andExpect(jsonPath("$.message").value("Authentication is required"));
        }

        @Test
        @WithMockMyUserDetails
        public void deleteCommentSuccess() throws Exception {
                Long postId = 1L;
                Long commentId = 1L;
                Long userId = 1L;

                doNothing().when(commentService).delete(commentId, postId, userId);

                mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", postId, commentId))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.code").value("SUCCESS"))
                                .andExpect(jsonPath("$.message").value("Comment deleted successfully"));
        }

        @Test
        public void deleteCommentUnauthorized() throws Exception {
                Long postId = 1L;
                Long commentId = 1L;

                mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", postId, commentId))
                                .andExpect(status().isUnauthorized())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.code").value("FAILURE"))
                                .andExpect(jsonPath("$.message").value("Authentication is required"));
        }
}
