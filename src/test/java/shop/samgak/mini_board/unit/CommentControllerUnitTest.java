package shop.samgak.mini_board.unit;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                                .andExpect(jsonPath("$.data.length()").value(2))
                                .andExpect(jsonPath("$.data[0].content").value("First Comment"))
                                .andExpect(jsonPath("$.data[1].content").value("Second Comment"))
                                .andExpect(jsonPath("$.code").value("SUCCESS"));
        }

        @Test
        @WithMockMyUserDetails
        public void testGetCommentByIdNotFound() throws Exception {
                Long postId = 1L;

                when(commentService.get(postId))
                                .thenThrow(new ResourceNotFoundException("Comment not found with Post ID : " + postId));

                mockMvc.perform(get("/api/posts/{postId}/comments", postId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.code").value("FAILURE"))
                                .andExpect(jsonPath("$.message").value("Comment not found with Post ID : " + postId));
        }

        @Test
        public void createCommentUserNotFound() throws Exception {
                String content = "Test Content";
                Long postId = 1L;

                // 인증되지 않은 상태에서 댓글 생성 요청
                mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                                .param("content", content))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockMyUserDetails
        public void createCommentMissingContent() throws Exception {
                Long postId = 1L;

                mockMvc.perform(post("/api/posts/{postId}/comments", postId))
                                .andExpect(status().isBadRequest());
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

                mockMvc.perform(post("/api/posts/{commentId}/comments", postId)
                                .param("content", content))
                                .andExpect(status().isCreated());
        }

        @Test
        @WithMockMyUserDetails
        public void updateCommentSuccess() throws Exception {
                Long postId = 1L;
                String content = "Updated Content";
                Long commentId = 1L;

                doNothing().when(commentService).update(commentId, content, 1L);

                mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", postId, commentId)
                                .param("content", content))
                                .andExpect(status().isOk()) // HTTP 상태 코드 200 확인
                                .andExpect(jsonPath("$.code").value("SUCCESS")) // 응답 코드가 SUCCESS인지 확인
                                .andExpect(jsonPath("$.message").value("Comment updated successfully")); // 성공 메시지 확인
        }

        @Test
        public void updateCommentUnauthorized() throws Exception {
                Long postId = 1L;
                Long commentId = 1L;
                String content = "Updated Content";

                mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", postId, commentId)
                                .param("content", content))
                                .andExpect(status().isUnauthorized()); // 인증되지 않은 경우 401 응답 기대
        }

        @Test
        @WithMockMyUserDetails
        public void deleteCommentSuccess() throws Exception {
                Long postId = 1L;
                Long commentId = 1L;

                doNothing().when(commentService).delete(commentId, 1L);

                mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", postId, commentId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value("SUCCESS"))
                                .andExpect(jsonPath("$.message").value("Comment deleted successfully"));
        }

        @Test
        public void deleteCommentUnauthorized() throws Exception {
                Long postId = 1L;
                Long commentId = 1L;

                mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", postId, commentId))
                                .andExpect(status().isUnauthorized());
        }
}
