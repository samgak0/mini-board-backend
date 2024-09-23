package shop.samgak.mini_board.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentLikeDTO {
    private Long id;
    private UserDTO user;
    private CommentDTO comment;
    private LocalDateTime createdAt;
}
