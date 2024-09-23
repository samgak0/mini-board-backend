package shop.samgak.mini_board.post.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.samgak.mini_board.user.dto.UserDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeDTO {
    private Long id;
    private UserDTO user;
    private PostDTO post;
    private LocalDateTime createdAt;
}
