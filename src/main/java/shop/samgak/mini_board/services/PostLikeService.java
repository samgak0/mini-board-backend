package shop.samgak.mini_board.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.dto.PostLikeDTO;
import shop.samgak.mini_board.repositories.PostLikeRepository;

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
