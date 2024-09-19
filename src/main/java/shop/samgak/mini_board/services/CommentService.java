package shop.samgak.mini_board.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.entities.Comment;
import shop.samgak.mini_board.repositories.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentService {
    final CommentRepository commentRepository;

    public List<Comment> getAll() {
        return commentRepository.findAll();
    }
}
