package shop.samgak.mini_board.comment.services;

import java.util.List;

import shop.samgak.mini_board.comment.dto.CommentDTO;

public interface CommentService {
    List<CommentDTO> getAll();
}
