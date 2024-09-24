package shop.samgak.mini_board.post.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.post.Repositories.PostRepository;
import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.post.mapper.PostMapper;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    final PostRepository postRepository;
    final PostMapper postMapper;

    @Override
    public List<PostDTO> getAll() {
        return postRepository.findAll().stream()
                .map(postMapper::postToPostDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PostDTO getPostById(Long id) {
        return postRepository.findById(id)
                .map(postMapper::postToPostDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
    }
}
