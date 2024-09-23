<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/entities/CommentFile.java
package shop.samgak.mini_board.comment.entities;
========
package shop.samgak.mini_board.comment;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentFile.java

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "posts_files")
@Data
public class CommentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @JoinColumn(name = "originalName")
    private String original_name;

    @JoinColumn(name = "file_path")
    private String filePath;

    @JoinColumn(name = "file_size")
    private Long fileSize;

    @JoinColumn(name = "created_at")
    private LocalDateTime createdAt;

}
