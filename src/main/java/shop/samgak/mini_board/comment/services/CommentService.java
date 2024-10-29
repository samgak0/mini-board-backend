package shop.samgak.mini_board.comment.services;

import java.util.List;

import shop.samgak.mini_board.comment.dto.CommentDTO;

public interface CommentService {
        List<CommentDTO> get(Long postId);

        CommentDTO create(String content, Long postId, Long userId);

        void update(Long commentId, String content, Long userId);

        void delete(Long commentId, Long userId);
}
