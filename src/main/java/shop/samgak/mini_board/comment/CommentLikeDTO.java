<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/dto/CommentLikeDTO.java
package shop.samgak.mini_board.comment.dto;
========
package shop.samgak.mini_board.comment;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentLikeDTO.java

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/dto/CommentLikeDTO.java
import shop.samgak.mini_board.user.dto.UserDTO;
========
import shop.samgak.mini_board.user.UserDTO;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentLikeDTO.java

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentLikeDTO {
    private Long id;
    private UserDTO user;
    private CommentDTO comment;
    private LocalDateTime createdAt;
}
