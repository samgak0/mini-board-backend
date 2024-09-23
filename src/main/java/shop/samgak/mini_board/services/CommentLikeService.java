package shop.samgak.mini_board.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.dto.CommentLikeDTO;
import shop.samgak.mini_board.repositories.CommentLikeRepository;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    final CommentLikeRepository commentLikeRepository;
    final ModelMapper modelMapper;

    public List<CommentLikeDTO> getAll() {
        return commentLikeRepository.findAll().stream()
                .map(commentLike -> modelMapper.map(commentLike, CommentLikeDTO.class))
                .collect(Collectors.toList());
    }
}
