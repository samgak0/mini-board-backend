package shop.samgak.mini_board.entity;

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
public class PostFiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @JoinColumn(name = "originalName")
    private String original_name;

    @JoinColumn(name = "file_path")
    private String filePath;

    @JoinColumn(name = "file_size")
    private Long fileSize;

    @JoinColumn(name = "created_at")
    private LocalDateTime createdAt;

}
