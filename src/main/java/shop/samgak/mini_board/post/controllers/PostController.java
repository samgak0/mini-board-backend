package shop.samgak.mini_board.post.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.exceptions.MissingParameterException;
import shop.samgak.mini_board.post.services.PostService;
import shop.samgak.mini_board.user.entities.User;
import shop.samgak.mini_board.utility.ApiDataResponse;
import shop.samgak.mini_board.utility.ApiResponse;
import shop.samgak.mini_board.utility.UserSessionHelper;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final UserSessionHelper userSessionHelper;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllPost() {
        return ResponseEntity.ok(new ApiDataResponse("success", postService.getAll(), true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiDataResponse("success", postService.getPostById(id), true));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createPost(@RequestParam String title, @RequestParam String content,
            HttpSession session) {
        if (title == null || title.isEmpty())
            throw new MissingParameterException("username");
        if (content == null || content.isEmpty())
            throw new MissingParameterException("content");
        try {
            User user = userSessionHelper.getCurrentUserFromSession(session).orElseThrow();
            Long createdId = postService.create(title, content, user);
            URI location = URI.create(String.format("/api/post/%d", createdId));
            return ResponseEntity.created(location).body(new ApiResponse("success", true));
        } catch (RuntimeException | JsonProcessingException e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(e.getMessage(), false));
        }
    }
}
