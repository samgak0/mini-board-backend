<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/dto/CommentFileDTO.java
package shop.samgak.mini_board.comment.dto;
========
package shop.samgak.mini_board.comment;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentFileDTO.java

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentFileDTO {
    private Long id;
    private CommentDTO comment;
    private String originalName;
    private String filePath;
    private Long fileSize;
    private LocalDateTime createdAt;
}
