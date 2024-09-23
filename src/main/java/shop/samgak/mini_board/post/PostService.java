<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/services/PostService.java
package shop.samgak.mini_board.post.services;
========
package shop.samgak.mini_board.post;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostService.java

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/services/PostService.java
import shop.samgak.mini_board.post.Repositories.PostRepository;
import shop.samgak.mini_board.post.dto.PostDTO;
========
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostService.java

@RequiredArgsConstructor
public class PostService {
    final PostRepository postRepository;
    final ModelMapper modelMapper;

    public List<PostDTO> getAll() {
        return postRepository.findAll().stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .collect(Collectors.toList());
    }

    public PostDTO getPostById(Long id) {
        return postRepository.findById(id)
                .map(post -> modelMapper.map(post, PostDTO.class))
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
    }
}
