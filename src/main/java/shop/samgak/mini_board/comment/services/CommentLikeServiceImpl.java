package shop.samgak.mini_board.comment.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.comment.dto.CommentLikeDTO;
import shop.samgak.mini_board.comment.repository.CommentLikeRepository;

@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements CommentLikeService {
    final CommentLikeRepository commentLikeRepository;
    final ModelMapper modelMapper;

    @Override
    public List<CommentLikeDTO> getAll() {
        return commentLikeRepository.findAll().stream()
                .map(commentLike -> modelMapper.map(commentLike, CommentLikeDTO.class))
                .collect(Collectors.toList());
    }
}
