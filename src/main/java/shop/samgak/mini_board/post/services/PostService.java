package shop.samgak.mini_board.post.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.post.Repositories.PostRepository;
import shop.samgak.mini_board.post.dto.PostDTO;

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
