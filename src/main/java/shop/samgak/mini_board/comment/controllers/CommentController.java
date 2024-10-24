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

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/comments/")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse> getComment(@PathVariable("id") Long postId) {
        return ResponseEntity.ok(new ApiDataResponse("success", commentService.get(postId), true));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createComment(@RequestParam("postId") Long postId,
            @RequestParam("content") String content) {

        UserDTO userDTO = AuthUtils.getCurrentUser();
        CommentDTO savedComment = commentService.create(content, postId, userDTO.getId());

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/users/{id}")
                .buildAndExpand(savedComment.getId())
                .toUri();
        return ResponseEntity.created(location)
                .body(new ApiDataResponse("Comment created successfully", savedComment, true));

    }

    @PutMapping("{id}")
    public ResponseEntity<ApiResponse> updateComment(@PathVariable("id") Long commentId,
            @RequestParam("content") String content) {

        UserDTO userDTO = AuthUtils.getCurrentUser();
        commentService.update(commentId, content, userDTO.getId());
        return ResponseEntity.ok(new ApiResponse("Comment updated successfully", true));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable("id") Long commentId) {

        UserDTO userDTO = AuthUtils.getCurrentUser();
        commentService.delete(commentId, userDTO.getId());
        return ResponseEntity.ok(new ApiResponse("Comment deleted successfully", true));

    }
}
