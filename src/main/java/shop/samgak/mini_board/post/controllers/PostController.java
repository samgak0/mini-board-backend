package shop.samgak.mini_board.post.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.exceptions.MissingParameterException;
import shop.samgak.mini_board.post.services.PostService;
import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.utility.ApiDataResponse;
import shop.samgak.mini_board.utility.ApiResponse;
import shop.samgak.mini_board.utility.AuthUtils;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllPost() {
        return ResponseEntity.ok(new ApiDataResponse("success", postService.getAll(), true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPost(@PathVariable("id") Long id) {
        return ResponseEntity.ok(new ApiDataResponse("success", postService.getPostById(id), true));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createPost(@RequestParam String title, @RequestParam String content) {
        if (title == null || title.isEmpty())
            throw new MissingParameterException("username");
        if (content == null || content.isEmpty())
            throw new MissingParameterException("content");
        try {
            UserDTO userDTO = AuthUtils.getCurrentUser().orElseThrow();
            Long createdId = postService.create(title, content, userDTO);
            URI location = URI.create(String.format("/api/post/%d", createdId));
            return ResponseEntity.created(location).body(new ApiResponse("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(e.getMessage(), false));
        }
    }
}
