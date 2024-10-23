package shop.samgak.mini_board.comment.services;

import java.util.List;

import shop.samgak.mini_board.comment.dto.CommentDTO;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.exceptions.UnauthorizedActionException;

public interface CommentService {
        List<CommentDTO> get(Long postId);

        CommentDTO create(String content, Long postId, Long userId);

        void update(Long commentId, String content, Long userId)
                        throws ResourceNotFoundException, UnauthorizedActionException;

        void delete(Long commentId, Long userId)
                        throws ResourceNotFoundException, UnauthorizedActionException;
}
