package shop.samgak.mini_board.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostLikeDTO {
    private Long id; 
    private UserDTO user; 
    private PostDTO post;
    private LocalDateTime createdAt;
}
