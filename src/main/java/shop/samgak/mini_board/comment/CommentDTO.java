<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/dto/CommentDTO.java
package shop.samgak.mini_board.comment.dto;
========
package shop.samgak.mini_board.comment;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentDTO.java

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/dto/CommentDTO.java
import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.user.dto.UserDTO;
========
import shop.samgak.mini_board.post.PostDTO;
import shop.samgak.mini_board.user.UserDTO;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentDTO.java

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
