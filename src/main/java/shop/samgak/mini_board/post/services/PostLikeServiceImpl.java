package shop.samgak.mini_board.post.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.post.Repositories.PostLikeRepository;
import shop.samgak.mini_board.post.dto.PostLikeDTO;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    final PostLikeRepository postLikeRepository;
    final ModelMapper modelMapper;

    @Override
    public List<PostLikeDTO> getAll() {
        return postLikeRepository.findAll().stream()
                .map(postLike -> modelMapper.map(postLike, PostLikeDTO.class))
                .collect(Collectors.toList());
    }
}
