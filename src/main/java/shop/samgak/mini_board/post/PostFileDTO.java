<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/dto/PostFileDTO.java
package shop.samgak.mini_board.post.dto;
========
package shop.samgak.mini_board.post;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostFileDTO.java

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostFileDTO {
    private Long id;
    private PostDTO post;
    private String originalName;
    private String filePath;
    private Long fileSize;
    private LocalDateTime createdAt;
}
