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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
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
                // 각 테스트 실행 전 설정이 필요하다면 여기에 추가
        }

        @AfterEach
        public void cleanUp() {
                // 각 테스트 실행 후 정리가 필요하다면 여기에 추가
        }

        // 게시글 상위 10개 조회 테스트
        @Test
        public void testGetTop10Posts() throws Exception {
                // 테스트용 Mock 데이터 생성
                List<PostDTO> mockPosts = new ArrayList<>();
                mockPosts.add(new PostDTO(1L, null, "First Post", "Content of the first post", 0L, Instant.now(),
                                Instant.now(), null,
                                null));
                mockPosts.add(new PostDTO(2L, null, "Second Post", "Content of the second post", 0L, Instant.now(),
                                Instant.now(), null,
                                null));

                // postService.getTop10() 호출 시 mockPosts 반환
                when(postService.getTop10()).thenReturn(mockPosts);

                // /api/posts 엔드포인트로 GET 요청 수행 및 응답 검증
                mockMvc.perform(get("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk()) // HTTP 상태 코드 200 확인
                                .andExpect(jsonPath("$.data.length()").value(2)) // 데이터의 길이가 2인지 확인
                                .andExpect(jsonPath("$.data[0].title").value("First Post")) // 첫 번째 게시글 제목 확인
                                .andExpect(jsonPath("$.data[1].title").value("Second Post")) // 두 번째 게시글 제목 확인
                                .andExpect(jsonPath("$.code").value("SUCCESS")); // 응답 코드 확인
        }

        // 특정 게시글 조회 테스트
        @Test
        public void testGetPostById() throws Exception {
                Long postId = 1L;

                // 테스트용 Mock 데이터 생성
                PostDTO mockPost = new PostDTO(postId, new UserDTO(1L, "user"), "First Post",
                                "Content of the first post", 0L, Instant.now(), Instant.now(), null, null);

                // postService.getPostById(postId) 호출 시 mockPost 반환
                when(postService.getPostById(postId)).thenReturn(mockPost);

                // /api/posts/{id} 엔드포인트로 GET 요청 수행 및 응답 검증
                mockMvc.perform(get("/api/posts/{id}", postId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk()) // HTTP 상태 코드 200 확인
                                .andExpect(jsonPath("$.data.title").value("First Post")) // 게시글 제목 확인
                                .andExpect(jsonPath("$.data.content").value("Content of the first post")) // 게시글 내용 확인
                                .andExpect(jsonPath("$.code").value("SUCCESS")); // 응답 코드 확인
        }

        // 게시글이 존재하지 않을 때 조회 테스트
        @Test
        public void testGetPostByIdNotFound() throws Exception {
                Long postId = 1L;

                // postService.getPostById(postId) 호출 시 예외 발생 설정
                when(postService.getPostById(postId))
                                .thenThrow(new ResourceNotFoundException("Post not found with id: " + postId));

                // /api/posts/{id} 엔드포인트로 GET 요청 수행 및 응답 검증
                mockMvc.perform(get("/api/posts/{id}", postId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound()) // HTTP 상태 코드 404 확인
                                .andExpect(jsonPath("$.message").value("Post not found with id: " + postId)) // 오류 메시지
                                                                                                             // 확인
                                .andExpect(jsonPath("$.code").value("FAILURE")); // 응답 코드 확인
        }

        // 사용자를 찾을 수 없을 때 게시글 생성 테스트
        @Test
        public void createPostUserNotFound() throws Exception {
                String title = "Test Title";
                String content = "Test Content";

                // /api/posts 엔드포인트로 POST 요청 수행 (인증 정보 없음) 및 응답 검증
                mockMvc.perform(post("/api/posts")
                                .param("title", title)
                                .param("content", content))
                                .andExpect(status().isUnauthorized()); // HTTP 상태 코드 401 확인
        }

        // 제목이 누락된 게시글 생성 테스트
        @Test
        @WithMockMyUserDetails
        public void createPostMissingTitle() throws Exception {
                String title = "Test Title";

                // 제목만 있고 내용이 없는 상태로 POST 요청 수행 및 응답 검증
                mockMvc.perform(post("/api/posts")
                                .param("title", title))
                                .andExpect(status().isBadRequest()); // HTTP 상태 코드 400 확인
        }

        // 내용이 누락된 게시글 생성 테스트
        @Test
        @WithMockMyUserDetails
        public void createPostMissingContent() throws Exception {
                String content = "Test Content";

                // 내용만 있고 제목이 없는 상태로 POST 요청 수행 및 응답 검증
                mockMvc.perform(post("/api/posts")
                                .param("content", content))
                                .andExpect(status().isBadRequest()); // HTTP 상태 코드 400 확인
        }

        // 제목과 내용이 모두 누락된 게시글 생성 테스트
        @Test
        @WithMockMyUserDetails
        public void createPostMissingTitleAndContent() throws Exception {
                // 제목과 내용 모두 없는 상태로 POST 요청 수행 및 응답 검증
                mockMvc.perform(post("/api/posts"))
                                .andExpect(status().isBadRequest()); // HTTP 상태 코드 400 확인
        }

        // 게시글 생성 성공 테스트
        @Test
        @WithMockMyUserDetails
        public void createPostSuccess() throws Exception {
                String title = "Test Title";
                String content = "Test Content";

                // 제목과 내용이 모두 있는 상태로 POST 요청 수행 및 응답 검증
                mockMvc.perform(post("/api/posts")
                                .param("title", title)
                                .param("content", content))
                                .andExpect(status().isCreated()); // HTTP 상태 코드 201 확인
        }

        // 게시글 수정 성공 테스트
        @Test
        @WithMockMyUserDetails
        public void updatePostSuccess() throws Exception {
                Long postId = 1L;
                String title = "Updated Title";
                String content = "Updated Content";

                // /api/posts/{id} 엔드포인트로 PUT 요청 수행 및 응답 검증
                mockMvc.perform(put("/api/posts/{id}", postId)
                                .param("title", title)
                                .param("content", content))
                                .andExpect(status().isOk()) // HTTP 상태 코드 200 확인
                                .andExpect(jsonPath("$.code").value("SUCCESS")); // 응답 코드 확인
        }

        // 권한이 없는 경우 게시글 수정 테스트
        @Test
        public void updatePostUnauthorized() throws Exception {
                Long postId = 1L;
                String title = "Updated Title";
                String content = "Updated Content";

                // 인증 정보 없이 PUT 요청 수행 및 응답 검증
                mockMvc.perform(put("/api/posts/{id}", postId)
                                .param("title", title)
                                .param("content", content))
                                .andExpect(status().isUnauthorized()); // HTTP 상태 코드 401 확인
        }

        // 게시글 삭제 성공 테스트
        @Test
        @WithMockMyUserDetails
        public void deletePostSuccess() throws Exception {
                Long postId = 1L;

                // /api/posts/{id} 엔드포인트로 DELETE 요청 수행 및 응답 검증
                mockMvc.perform(delete("/api/posts/{id}", postId))
                                .andExpect(status().isOk()) // HTTP 상태 코드 200 확인
                                .andExpect(jsonPath("$.code").value("SUCCESS")); // 응답 코드 확인
        }

        // 권한이 없는 경우 게시글 삭제 테스트
        @Test
        public void deletePostUnauthorized() throws Exception {
                Long postId = 1L;

                // 인증 정보 없이 DELETE 요청 수행 및 응답 검증
                mockMvc.perform(delete("/api/posts/{id}", postId))
                                .andExpect(status().isUnauthorized()); // HTTP 상태 코드 401 확인
        }
}
