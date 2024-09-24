package shop.samgak.mini_board.post.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.post.Repositories.PostLikeRepository;
import shop.samgak.mini_board.post.dto.PostLikeDTO;
import shop.samgak.mini_board.post.mapper.PostLikeMapper;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    final PostLikeRepository postLikeRepository;
    final PostLikeMapper postLikeMapper;

    @Override
    public List<PostLikeDTO> getAll() {
        return postLikeRepository.findAll().stream()
                .map(postLikeMapper::toDTO)
                .collect(Collectors.toList());
    }
}
