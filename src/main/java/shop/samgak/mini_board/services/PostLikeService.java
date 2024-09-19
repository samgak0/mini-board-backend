package shop.samgak.mini_board.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.entities.PostLike;
import shop.samgak.mini_board.repositories.PostLikeRepository;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    final PostLikeRepository postLikeRepository;

    public List<PostLike> getAll() {
        return postLikeRepository.findAll();
    }
}
