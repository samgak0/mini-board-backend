package shop.samgak.mini_board.comment.services;

import java.util.List;

import shop.samgak.mini_board.comment.dto.CommentLikeDTO;

public interface CommentLikeService {
    List<CommentLikeDTO> getAll();
}
