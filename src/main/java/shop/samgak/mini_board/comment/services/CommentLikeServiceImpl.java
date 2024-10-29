package shop.samgak.mini_board.comment.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.comment.dto.CommentLikeDTO;
import shop.samgak.mini_board.comment.mapper.CommentLikeMapper;
import shop.samgak.mini_board.comment.repository.CommentLikeRepository;

@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements CommentLikeService {
    final CommentLikeRepository commentLikeRepository;
    final CommentLikeMapper commentLikeMapper;

    @Override
    public List<CommentLikeDTO> getAll() {
        return commentLikeRepository.findAll().stream()
                .map(commentLikeMapper::toDTO)
                .collect(Collectors.toList());
    }
}
