package shop.samgak.mini_board.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.entities.Post;
import shop.samgak.mini_board.repositories.PostRepository;

@Service
@RequiredArgsConstructor
public class PostService {
    final PostRepository postRepository;

    public List<Post> getAll() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }
}
