package shop.samgak.mini_board.comment.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.samgak.mini_board.user.dto.UserDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentLikeDTO {
    private Long id;
    private UserDTO user;
    private CommentDTO comment;
    private LocalDateTime createdAt;
}
