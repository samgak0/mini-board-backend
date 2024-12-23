package shop.samgak.mini_board.comment.controllers;

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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.comment.dto.CommentDTO;
import shop.samgak.mini_board.comment.services.CommentService;
import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.utility.ApiDataResponse;
import shop.samgak.mini_board.utility.ApiResponse;
import shop.samgak.mini_board.utility.ApiSuccessResponse;
import shop.samgak.mini_board.utility.AuthUtils;

/**
 * 게시물 댓글 관련 API 요청을 처리하는 컨트롤러 클래스
 * 댓글 조회, 생성, 수정, 삭제 기능을 제공합
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts/")
public class CommentController {
        private final CommentService commentService;

        /**
         * 특정 게시물의 댓글 목록을 가져오는 메서드
         *
         * @param postId 댓글을 가져올 게시물의 ID
         * @return 댓글 목록과 성공 응답을 포함한 ResponseEntity 객체
         */
        @GetMapping("{postId}/comments")
        public ResponseEntity<ApiResponse> getComment(@PathVariable Long postId) {
                log.info("Retrieving the list of comments for post ID: [{}]", postId);
                return ResponseEntity.ok(new ApiDataResponse("success", commentService.get(postId)));
        }

        /**
         * 특정 게시물에 새로운 댓글을 추가하는 메서드
         *
         * @param postId  댓글을 추가할 게시물의 ID
         * @param content 추가할 댓글의 내용
         * @return 생성된 댓글 정보와 성공 응답을 포함한 ResponseEntity 객체
         */
        @PostMapping("{postId}/comments")
        public ResponseEntity<ApiResponse> createComment(@PathVariable Long postId,
                        @Valid @RequestBody CreateCommentRequest request) {
                UserDTO userDTO = AuthUtils.getCurrentUser();
                log.info("User with ID [{}] is creating a new comment on post with ID [{}]", userDTO.getId(), postId);
                CommentDTO savedComment = commentService.create(request.content, postId, userDTO.getId());

                URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/api/users/{id}")
                                .buildAndExpand(savedComment.getId())
                                .toUri();

                return ResponseEntity.created(location)
                                .body(new ApiDataResponse("Comment created successfully", savedComment));
        }

        /**
         * 특정 댓글 수정 메서드
         *
         * @param commentId 수정할 댓글의 ID
         * @param content   수정할 댓글의 내용
         * @return 수정 성공 응답을 포함한 ResponseEntity 객체
         */
        @PutMapping("{postId}/comments/{commentId}")
        public ResponseEntity<ApiResponse> updateComment(@PathVariable Long postId, @PathVariable Long commentId,
                        @Valid @RequestBody UpdateCommentRequest request) {
                UserDTO userDTO = AuthUtils.getCurrentUser();
                log.info("User with ID [{}] is editing comment with ID [{}]", userDTO.getId(), commentId);
                commentService.update(commentId, postId, request.content, userDTO.getId());
                return ResponseEntity.ok(new ApiSuccessResponse("Comment updated successfully"));
        }

        /**
         * 특정 댓글 삭제 메서드
         *
         * @param commentId 삭제할 댓글의 ID
         * @return 삭제 성공 응답을 포함한 ResponseEntity 객체
         */
        @DeleteMapping("{postId}/comments/{commentId}")
        public ResponseEntity<ApiResponse> deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
                UserDTO userDTO = AuthUtils.getCurrentUser();
                log.info("User with ID [{}] is deleting comment with ID [{}]", userDTO.getId(), commentId);
                commentService.delete(commentId, postId, userDTO.getId());
                return ResponseEntity.ok(new ApiSuccessResponse("Comment deleted successfully"));
        }

        public record CreateCommentRequest(
                        @NotNull(message = "Missing required parameter") String content) {
        }

        public record UpdateCommentRequest(
                        @NotNull(message = "Missing required parameter") String content) {
        }
}
