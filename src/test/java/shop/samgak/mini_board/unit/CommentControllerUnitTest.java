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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
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
                // 각 테스트 실행 전 설정 작업 수행 (예: Mock 데이터 초기화)
        }

        @AfterEach
        public void cleanUp() {
                // 각 테스트 실행 후 정리 작업 수행 (예: 리소스 해제 등)
        }

        @Test
        @WithMockMyUserDetails
        public void testGetComments() throws Exception {
                Long postId = 1L;

                // 게시물의 댓글 리스트를 조회하는 테스트
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

                // 모의 데이터를 리스트에 추가
                mockComments.add(comment1);
                mockComments.add(comment2);
                when(commentService.get(1L)).thenReturn(mockComments);

                // 댓글 리스트를 요청하고, 기대하는 결과를 검증합니다.
                mockMvc.perform(get("/api/posts/{postId}/comments", postId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk()) // 요청이 성공적이어야 합니다 (HTTP 200)
                                .andExpect(jsonPath("$.data.length()").value(2)) // 데이터 길이가 2인지 확인
                                .andExpect(jsonPath("$.data[0].content").value("First Comment")) // 첫 번째 댓글의 내용 확인
                                .andExpect(jsonPath("$.data[1].content").value("Second Comment")) // 두 번째 댓글의 내용 확인
                                .andExpect(jsonPath("$.code").value("SUCCESS")); // 응답 코드가 SUCCESS인지 확인
        }

        @Test
        @WithMockMyUserDetails
        public void testGetCommentByIdNotFound() throws Exception {
                // 존재하지 않는 댓글을 조회할 때 발생하는 오류를 테스트
                Long postId = 1L;

                // 해당 ID의 댓글이 없을 경우 예외를 발생하도록 설정
                when(commentService.get(postId))
                                .thenThrow(new ResourceNotFoundException("Comment not found with Post ID : " + postId));

                // 요청하고, 404 오류가 발생하는지 검증합니다.
                mockMvc.perform(get("/api/posts/{postId}/comments", postId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound()) // HTTP 상태 코드 404 확인
                                .andExpect(jsonPath("$.code").value("FAILURE")) // 응답 코드가 FAILURE인지 확인
                                .andExpect(jsonPath("$.message").value("Comment not found with Post ID : " + postId)); // 오류
                                                                                                                       // 메시지
                                                                                                                       // 확인
        }

        @Test
        public void createCommentUserNotFound() throws Exception {
                // 인증되지 않은 사용자가 댓글을 생성하려고 할 때의 테스트
                String content = "Test Content";
                Long postId = 1L;

                // 인증되지 않은 상태에서 댓글 생성 요청
                mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                                .param("content", content))
                                .andExpect(status().isUnauthorized()); // 인증되지 않은 경우 401 응답 기대
        }

        @Test
        @WithMockMyUserDetails
        public void createCommentMissingContent() throws Exception {
                // 댓글 내용이 없을 때의 오류를 테스트
                Long postId = 1L;

                // 빈 내용으로 댓글 생성 요청
                mockMvc.perform(post("/api/posts/{postId}/comments", postId))
                                .andExpect(status().isBadRequest()); // 잘못된 요청으로 400 응답 기대
        }

        @Test
        @WithMockMyUserDetails
        public void createCommentSuccess() throws Exception {
                // 댓글을 정상적으로 생성하는 경우 테스트
                String content = "Test Content";
                Long postId = 1L;
                Long userId = 1L;
                Long commentId = 1L;

                // 댓글 DTO 설정 (테스트용 Mock 데이터)
                CommentDTO commentDTO = new CommentDTO();
                commentDTO.setId(commentId);
                commentDTO.setContent(content);
                commentDTO.setUser(new UserDTO(userId, "username"));
                commentDTO.setPost(
                                new PostDTO(2L, new UserDTO(userId, "username"), "Sample Title", "Sample Content", 0L,
                                                Instant.now(), Instant.now(), null, null));
                commentDTO.setCreatedAt(Instant.now());
                commentDTO.setUpdatedAt(Instant.now());

                // 댓글 생성 요청에 대한 Mock 설정
                when(commentService.create(content, postId, userId)).thenReturn(commentDTO);

                // 댓글 생성 요청 및 결과 검증
                mockMvc.perform(post("/api/posts/{commentId}/comments", postId)
                                .param("content", content))
                                .andExpect(status().isCreated()); // 성공적으로 생성되면 201 응답 기대
        }

        @Test
        @WithMockMyUserDetails
        public void updateCommentSuccess() throws Exception {
                // 댓글을 정상적으로 수정하는 경우 테스트
                Long postId = 1L;
                String content = "Updated Content";
                Long commentId = 1L;

                // Mock 설정: 댓글 업데이트 시 아무 작업도 하지 않도록 설정
                doNothing().when(commentService).update(commentId, content, 1L);

                // 댓글 수정 요청 및 결과 검증
                mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", postId, commentId)
                                .param("content", content))
                                .andExpect(status().isOk()) // HTTP 상태 코드 200 확인
                                .andExpect(jsonPath("$.code").value("SUCCESS")) // 응답 코드가 SUCCESS인지 확인
                                .andExpect(jsonPath("$.message").value("Comment updated successfully")); // 성공 메시지 확인
        }

        @Test
        public void updateCommentUnauthorized() throws Exception {
                // 인증되지 않은 사용자가 댓글을 수정하려 할 때의 테스트
                Long postId = 1L;
                Long commentId = 1L;
                String content = "Updated Content";

                // 인증 없이 댓글 수정 요청
                mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", postId, commentId)
                                .param("content", content))
                                .andExpect(status().isUnauthorized()); // 인증되지 않은 경우 401 응답 기대
        }

        @Test
        @WithMockMyUserDetails
        public void deleteCommentSuccess() throws Exception {
                // 댓글을 정상적으로 삭제하는 경우 테스트
                Long postId = 1L;
                Long commentId = 1L;

                // Mock 설정: 댓글 삭제 시 아무 작업도 하지 않도록 설정
                doNothing().when(commentService).delete(commentId, 1L);

                // 댓글 삭제 요청 및 결과 검증
                mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", postId, commentId))
                                .andExpect(status().isOk()) // HTTP 상태 코드 200 확인
                                .andExpect(jsonPath("$.code").value("SUCCESS")) // 응답 코드가 SUCCESS인지 확인
                                .andExpect(jsonPath("$.message").value("Comment deleted successfully")); // 성공 메시지 확인
        }

        // 인증되지 않은 사용자가 댓글을 삭제하려 할 때의 테스트
        @Test
        public void deleteCommentUnauthorized() throws Exception {
                Long postId = 1L;
                Long commentId = 1L;

                // 인증 없이 댓글 삭제 요청
                mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", postId, commentId))
                                .andExpect(status().isUnauthorized()); // 인증되지 않은 경우 401 응답 기대
        }
}
