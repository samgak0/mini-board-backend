package shop.samgak.mini_board.post.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.post.services.PostService;
import shop.samgak.mini_board.utility.ApiResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts")
public class PostController {

    final PostService postService;

    @GetMapping
    public List<PostDTO> getAllPost() {
        return postService.getAll();
    }

    @GetMapping("/{id}")
    public PostDTO getPost(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createPost(@RequestBody PostDTO postDTO) {
        PostDTO createdPostDTO = postService.create(postDTO);
        URI location = URI.create(String.format("/api/post/%d", createdPostDTO.getId()));
        return ResponseEntity.created(location).body(new ApiResponse("success", true));
    }
}
