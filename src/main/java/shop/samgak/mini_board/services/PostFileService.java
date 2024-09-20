package shop.samgak.mini_board.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.dto.PostDTO;
import shop.samgak.mini_board.entities.Post;
import shop.samgak.mini_board.repositories.PostRepository;

@Service
@RequiredArgsConstructor
public class PostFileService {
    final PostRepository postRepository;

    public List<PostDTO> getAll() {
        return postRepository.findAll().stream().map(Post::toDTO).collect(Collectors.toList());
    }
}
