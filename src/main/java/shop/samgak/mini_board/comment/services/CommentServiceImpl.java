package shop.samgak.mini_board.comment.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.comment.dto.CommentDTO;
import shop.samgak.mini_board.comment.mapper.CommentMapper; // MapStruct Mapper 추가
import shop.samgak.mini_board.comment.repository.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    final CommentRepository commentRepository;
    final CommentMapper commentMapper; // MapStruct Mapper 사용

    @Override
    public List<CommentDTO> getAll() {
        return commentRepository.findAll().stream()
                .map(commentMapper::toDTO) // MapStruct 매핑 사용
                .collect(Collectors.toList());
    }
}
