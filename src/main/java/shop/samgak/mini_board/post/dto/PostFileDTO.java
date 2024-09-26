package shop.samgak.mini_board.post.dto;

import java.time.Instant;

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
    private Instant createdAt;
}
