package shop.samgak.mini_board.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostFileDTO {
    private Long id;
    private PostDTO post;
    private String originalName;
    private String filePath;
    private Long fileSize;
    private LocalDateTime createdAt;
}
