package shop.samgak.mini_board.post.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.exceptions.MissingParameterException;
import shop.samgak.mini_board.post.services.PostService;
import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.utility.ApiDataResponse;
import shop.samgak.mini_board.utility.ApiResponse;
import shop.samgak.mini_board.utility.ApiSuccessResponse;
import shop.samgak.mini_board.utility.AuthUtils;

/**
 * 게시물 관련 API 요청을 처리하는 컨트롤러 정의
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    /**
     * 상위 10개의 게시물을 가져오는 엔드포인트
     * 
     * @return 상위 10개의 게시물 정보
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getTop10Post() {
        log.info("Request to get top 10 posts");
        return ResponseEntity.ok(new ApiDataResponse("success", postService.getTop10()));
    }

    /**
     * 특정 게시물을 가져오는 엔드포인트
     * 
     * @param postId  게시물 ID
     * @param session 현재 세션 객체 (조회수 증가용)
     * @return 특정 게시물의 상세 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPost(@PathVariable("id") Long postId, HttpSession session) {
        log.info("Request to get post with ID: {}", postId);

        // 게시물 조회수를 증가시킴
        boolean isUpdated = postService.increaseViewCount(postId, session);
        if (isUpdated) {
            log.info("View count increased for post ID: [{}]", postId);
        }

        return ResponseEntity.ok(new ApiDataResponse("success", postService.getPostById(postId)));
    }

    /**
     * 새로운 게시물을 생성하는 엔드포인트
     * 
     * @param title   게시물 제목
     * @param content 게시물 내용
     * @return 생성된 게시물의 위치 URI와 성공 응답
     */
    @PostMapping
    public ResponseEntity<ApiResponse> createPost(@Valid @RequestBody CreatePostRequest createPostRequest) {
        UserDTO userDTO = AuthUtils.getCurrentUser();
        log.info("Request to create a new post by user ID: [{}]", userDTO.getId());
        // 게시물을 생성하고 ID를 반환받음
        Long createdId = postService.create(createPostRequest.title, createPostRequest.content, userDTO);
        // 생성된 게시물의 URI 반환
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdId)
                .toUri();
        return ResponseEntity.created(location).body(new ApiSuccessResponse());
    }

    /**
     * 특정 게시물을 업데이트하는 엔드포인트
     * 
     * @param id      게시물 ID
     * @param title   업데이트할 게시물 제목
     * @param content 업데이트할 게시물 내용
     * @return 성공 여부 응답
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse> updatePost(@PathVariable("postId") Long id,
            @Valid @RequestBody UpdatePostRequest updatePostRequest) {
        if (updatePostRequest.title == null && updatePostRequest.content == null) {
            throw new MissingParameterException("There must be a title or content");
        }
        UserDTO userDTO = AuthUtils.getCurrentUser();
        log.info("Request to update post with post ID: [{}] by user ID: [{}]", id, userDTO.getId());
        postService.update(id, updatePostRequest.title, updatePostRequest.content, userDTO);
        return ResponseEntity.ok(new ApiSuccessResponse());
    }

    /**
     * 특정 게시물을 삭제하는 엔드포인트
     * 
     * @param id 게시물 ID
     * @return 성공 여부 응답
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable Long id) {
        // 현재 로그인된 사용자 정보 가져옴
        UserDTO userDTO = AuthUtils.getCurrentUser();
        log.info("Request to delete post with ID: [{}] by user: [{}]", id, userDTO.getUsername());
        postService.delete(id, userDTO);
        return ResponseEntity.ok(new ApiSuccessResponse());
    }

    /**
     * 게시글 수정 요청을 위한 레코드 정의
     */
    public record UpdatePostRequest(
            String title,
            String content) {
    }

    /**
     * 게시글 생성 요청을 위한 레코드 정의
     */
    public record CreatePostRequest(
            @NotNull(message = "Missing required parameter") String title,
            @NotNull(message = "Missing required parameter") String content) {
    }
}
