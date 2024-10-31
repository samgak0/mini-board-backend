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

/**
 * 게시물에 첨부된 파일 정보를 저장하는 엔티티 클래스
 */
@Entity
@Table(name = "post_files")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostFile {

    /**
     * 파일 ID (기본 키)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_files_seq")
    @SequenceGenerator(name = "post_files_seq", sequenceName = "POST_FILES_SEQ", allocationSize = 1)
    private Long id;

    /**
     * 파일이 속한 게시물 (게시물과의 관계 매핑)
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id", nullable = false, updatable = false)
    private Post post;

    /**
     * 파일의 원본 이름
     */
    @Column(name = "original_name", nullable = false, updatable = false)
    private String originalName;

    /**
     * 서버에 저장된 파일 이름 (UUID로 저장)
     */
    @Column(name = "file_name", nullable = false, updatable = false)
    private String fileName;

    /**
     * 파일의 콘텐츠 타입 (MIME 타입)
     */
    @Column(name = "content_type", nullable = false, updatable = false)
    private String contentType;

    /**
     * 파일 크기 (바이트 단위)
     */
    @Column(name = "file_size", nullable = false, updatable = false)
    private Long fileSize;

    /**
     * 파일 조회수
     */
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    /**
     * 파일 삭제 여부 (논리적 삭제)
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    /**
     * 파일 생성 일시
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
