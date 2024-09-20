package shop.samgak.mini_board.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.dto.PostLikeDTO;
import shop.samgak.mini_board.entities.PostLike;
import shop.samgak.mini_board.repositories.PostLikeRepository;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    final PostLikeRepository postLikeRepository;

    public List<PostLikeDTO> getAll() {
        return postLikeRepository.findAll().stream().map(PostLike::toDTO).collect(Collectors.toList());
    }
}
