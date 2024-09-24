package shop.samgak.mini_board.comment.services;

import java.util.List;

import shop.samgak.mini_board.comment.dto.CommentFileDTO;

public interface CommentFileService {
    List<CommentFileDTO> getAll();
}
