package shop.samgak.mini_board.comment.entities;

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
import lombok.Data;

/**
 * 댓글에 첨부된 파일 정보를 저장하는 엔티티 클래스
 * 댓글에 업로드된 파일의 메타데이터를 관리
 */
@Entity
@Table(name = "comment_files")
@Data
public class CommentFile {

    /**
     * 댓글 파일의 고유 ID
     * 데이터베이스 시퀀스를 사용하여 자동으로 생성
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_files_seq")
    @SequenceGenerator(name = "comment_files_seq", sequenceName = "COMMENT_FILES_SEQ", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 이 파일이 속한 댓글
     * 댓글과 다대일(Many-to-One) 관계입니다.
     */
    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    /**
     * 파일의 원래 이름 (사용자가 업로드한 파일명)
     */
    @Column(name = "original_name", nullable = false)
    private String originalName;

    /**
     * 파일이 서버에 저장된 경로
     */
    @Column(name = "file_path", nullable = false)
    private String filePath;

    /**
     * 파일의 크기 (바이트 단위)
     */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    /**
     * 파일이 생성된 시간 (업로드된 시간)
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

}
