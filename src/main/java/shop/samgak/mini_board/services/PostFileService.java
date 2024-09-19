package shop.samgak.mini_board.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.entities.Post;
import shop.samgak.mini_board.repositories.PostRepository;

@Service
@RequiredArgsConstructor
public class PostFileService {
    final PostRepository postRepository;

    public List<Post> getAll() {
        return postRepository.findAll();
    }
}
