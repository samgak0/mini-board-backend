package shop.samgak.mini_board.dto;

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
