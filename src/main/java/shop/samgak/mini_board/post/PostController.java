package shop.samgak.mini_board.post;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts")
public class PostController {

    final PostService postService;
    final PostRepository postRepository;
    final ModelMapper modelMapper;

    @GetMapping
    public List<PostDTO> getAllPost() {
        return postService.getAll();
    }

    @GetMapping("/{id}")
    public PostDTO getPost(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @GetMapping("/0/{id}")
    public PostDTO getPost0(@PathVariable Long id) {
        return modelMapper.map(postRepository.findById(id).orElse(null), PostDTO.class);
    }
}
