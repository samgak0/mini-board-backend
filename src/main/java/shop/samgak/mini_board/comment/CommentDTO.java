package shop.samgak.mini_board.comment;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.samgak.mini_board.post.PostDTO;
import shop.samgak.mini_board.user.UserDTO;

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
