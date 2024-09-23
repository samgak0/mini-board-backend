package shop.samgak.mini_board.post;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    final PostLikeRepository postLikeRepository;
    final ModelMapper modelMapper;

    public List<PostLikeDTO> getAll() {
        return postLikeRepository.findAll().stream()
                .map(postLike -> modelMapper.map(postLike, PostLikeDTO.class))
                .collect(Collectors.toList());
    }
}
