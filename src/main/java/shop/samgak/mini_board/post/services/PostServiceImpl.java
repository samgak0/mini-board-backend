package shop.samgak.mini_board.post.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.post.Repositories.PostRepository;
import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.post.entities.Post;
import shop.samgak.mini_board.post.mapper.PostMapper;
import shop.samgak.mini_board.user.entities.User;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    final PostRepository postRepository;
    final PostMapper postMapper;

    @Override
    public List<PostDTO> getAll() {
        return postRepository.findAll().stream()
                .map(postMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PostDTO getPostById(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
    }

    @Override
    public Long create(String title, String body, User user) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(body);
        post.setUser(user);
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }
}
