package shop.samgak.mini_board.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.dto.PostDTO;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.repositories.PostRepository;

@Service
@RequiredArgsConstructor
public class PostService {
    final PostRepository postRepository;
    ModelMapper modelMapper = new ModelMapper();

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
