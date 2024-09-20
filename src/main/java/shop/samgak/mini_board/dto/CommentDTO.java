package shop.samgak.mini_board.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private UserDTO user;
    private PostDTO post;
    private CommentDTO parentComment;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
