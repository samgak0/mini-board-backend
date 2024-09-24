package shop.samgak.mini_board.post.entities;

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
public class PostFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @JoinColumn(name = "original_name")
    private String originalName;

    @JoinColumn(name = "file_path")
    private String filePath;

    @JoinColumn(name = "file_size")
    private Long fileSize;

    @JoinColumn(name = "created_at")
    private LocalDateTime createdAt;
}
