package shop.samgak.mini_board.post.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_files")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostFile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_files_seq")
    @SequenceGenerator(name = "post_files_seq", sequenceName = "SAMGAK.POST_FILES_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id", nullable = false, updatable = false)
    private Post post;

    @Column(name = "original_name", nullable = false, updatable = false)
    private String originalName;

    @Column(name = "file_name", nullable = false, updatable = false)
    private String fileName;

    @Column(name = "content_type", nullable = false, updatable = false)
    private String contentType;

    @Column(name = "file_size", nullable = false, updatable = false)
    private Long fileSize;

    @Column(name = "hit_count", nullable = false)
    private Long hitCount = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
