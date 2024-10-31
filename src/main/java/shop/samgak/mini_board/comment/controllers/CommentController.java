package shop.samgak.mini_board.comment.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.comment.dto.CommentDTO;
import shop.samgak.mini_board.comment.services.CommentService;
import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.utility.ApiDataResponse;
import shop.samgak.mini_board.utility.ApiResponse;
import shop.samgak.mini_board.utility.AuthUtils;

/**
 * 게시물 댓글 관련 API 요청을 처리하는 컨트롤러 클래스
 * 댓글 조회, 생성, 수정, 삭제 기능을 제공합
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts")
public class CommentController {
    private final CommentService commentService;

    /**
     * 특정 게시물의 댓글 목록을 가져오는 메서드
     *
     * @param postId 댓글을 가져올 게시물의 ID
     * @return 댓글 목록과 성공 응답을 포함한 ResponseEntity 객체
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse> getComment(@PathVariable("postId") Long postId) {
        log.info("게시물 ID {}의 댓글 목록 조회", postId);
        return ResponseEntity.ok(new ApiDataResponse("success", commentService.get(postId), true));
    }

    /**
     * 특정 게시물에 새로운 댓글을 추가하는 메서드
     *
     * @param postId  댓글을 추가할 게시물의 ID
     * @param content 추가할 댓글의 내용
     * @return 생성된 댓글 정보와 성공 응답을 포함한 ResponseEntity 객체
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse> createComment(@PathVariable("postId") Long postId,
            @RequestParam("content") String content) {
        UserDTO userDTO = AuthUtils.getCurrentUser();
        log.info("사용자 ID {}가 게시물 ID {}에 새로운 댓글 생성", userDTO.getId(), postId);
        CommentDTO savedComment = commentService.create(content, postId, userDTO.getId());

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/users/{id}")
                .buildAndExpand(savedComment.getId())
                .toUri();
        return ResponseEntity.created(location)
                .body(new ApiDataResponse("Comment created successfully", savedComment, true));
    }

    /**
     * 특정 댓글 수정 메서드
     *
     * @param commentId 수정할 댓글의 ID
     * @param content   수정할 댓글의 내용
     * @return 수정 성공 응답을 포함한 ResponseEntity 객체
     */
    @PutMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse> updateComment(@PathVariable("postId") Long commentId,
            @RequestParam("content") String content) {
        UserDTO userDTO = AuthUtils.getCurrentUser();
        log.info("사용자 ID {}가 댓글 ID {} 수정", userDTO.getId(), commentId);
        commentService.update(commentId, content, userDTO.getId());
        return ResponseEntity.ok(new ApiResponse("Comment updated successfully", true));
    }

    /**
     * 특정 댓글 삭제 메서드
     *
     * @param commentId 삭제할 댓글의 ID
     * @return 삭제 성공 응답을 포함한 ResponseEntity 객체
     */
    @DeleteMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable("postId") Long commentId) {
        UserDTO userDTO = AuthUtils.getCurrentUser();
        log.info("사용자 ID {}가 댓글 ID {} 삭제", userDTO.getId(), commentId);
        commentService.delete(commentId, userDTO.getId());
        return ResponseEntity.ok(new ApiResponse("Comment deleted successfully", true));
    }
}
