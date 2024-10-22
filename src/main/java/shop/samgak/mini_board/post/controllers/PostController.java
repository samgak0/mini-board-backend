package shop.samgak.mini_board.post.controllers;

import java.net.URI;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
import shop.samgak.mini_board.exceptions.MissingParameterException;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
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
    public static final String ERROR_AUTHENTICATION_REQUIRED = "Authentication is required";

    @GetMapping
    public ResponseEntity<ApiResponse> getTop10Post() {
        return ResponseEntity.ok(new ApiDataResponse("success", postService.getTop10(), true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPost(@PathVariable("id") Long id) {
        return ResponseEntity.ok(new ApiDataResponse("success", postService.getPostById(id), true));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createPost(@RequestParam("title") String title,
            @RequestParam("content") String content) {
        if (title == null || title.isEmpty())
            throw new MissingParameterException("title");
        if (content == null || content.isEmpty())
            throw new MissingParameterException("content");
        try {
            UserDTO userDTO = AuthUtils.getCurrentUser().orElseThrow();
            Long createdId = postService.create(title, content, userDTO);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdId)
                    .toUri();
            return ResponseEntity.created(location).body(new ApiResponse("success", true));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiDataResponse(ERROR_AUTHENTICATION_REQUIRED, null, false));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(e.getMessage(), false));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updatePost(@PathVariable("id") Long id,
            @RequestParam("title") String title, @RequestParam("content") String content) {
        if (id == null)
            throw new MissingParameterException("id");
        if (title == null || title.isEmpty())
            throw new MissingParameterException("title");
        if (content == null || content.isEmpty())
            throw new MissingParameterException("content");
        try {
            UserDTO userDTO = AuthUtils.getCurrentUser().orElseThrow();
            postService.update(id, title, content, userDTO);
            return ResponseEntity.ok(new ApiResponse("success", true));
        } catch (NoSuchElementException | ResourceNotFoundException | AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiDataResponse(ERROR_AUTHENTICATION_REQUIRED, e.getMessage(), false));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(e.getMessage(), false));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable("id") Long id) {
        if (id == null)
            throw new MissingParameterException("id");
        try {
            UserDTO userDTO = AuthUtils.getCurrentUser().orElseThrow();
            postService.delete(id, userDTO);
            return ResponseEntity.ok(new ApiResponse("success", true));
        } catch (NoSuchElementException | ResourceNotFoundException | AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiDataResponse(ERROR_AUTHENTICATION_REQUIRED, e.getMessage(), false));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(e.getMessage(), false));
        }
    }
}
