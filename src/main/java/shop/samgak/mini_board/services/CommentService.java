package shop.samgak.mini_board.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.dto.CommentDTO;
import shop.samgak.mini_board.entities.Comment;
import shop.samgak.mini_board.repositories.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentService {
    final CommentRepository commentRepository;

    public List<CommentDTO> getAll() {
        return commentRepository.findAll().stream().map(Comment::toDTO).collect(Collectors.toList());
    }
}
