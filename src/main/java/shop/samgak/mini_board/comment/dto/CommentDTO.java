package shop.samgak.mini_board.comment.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.user.dto.UserDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Long id;
    private UserDTO user;
    private PostDTO post;
    private CommentDTO parentComment;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
