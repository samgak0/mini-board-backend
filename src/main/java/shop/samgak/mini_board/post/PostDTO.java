<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/dto/PostDTO.java
package shop.samgak.mini_board.post.dto;
========
package shop.samgak.mini_board.post;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostDTO.java

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/dto/PostDTO.java
import shop.samgak.mini_board.user.dto.UserDTO;
========
import shop.samgak.mini_board.user.UserDTO;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostDTO.java

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long id;
    private UserDTO user;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
