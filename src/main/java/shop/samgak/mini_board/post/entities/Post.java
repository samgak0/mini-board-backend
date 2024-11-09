package shop.samgak.mini_board.post.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import shop.samgak.mini_board.user.entities.User;

/**
 * 게시물 정보를 저장하는 엔티티 클래스
 */
@Entity
@Table(name = "posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = { "user", "postFiles" })
public class Post {
    /**
     * 게시물 ID (기본 키)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq")
    @SequenceGenerator(name = "post_seq", sequenceName = "POSTS_SEQ", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 게시물 작성자 (사용자와의 관계 매핑)
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    /**
     * 게시물 제목
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * 게시물 내용 (긴 텍스트를 위한 LOB 필드)
     */
    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    /**
     * 게시물 조회수
     */
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    /**
     * 게시물 생성 일시
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    /**
     * 게시물 정보 마지막 수정 일시
     */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    /**
     * 게시물 삭제 여부 (논리적 삭제)
     */
    @Column(name = "deleted_at")
    private Instant deletedAt = null;

    /**
     * 게시물에 첨부된 파일 목록
     */
    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<PostFile> postFiles = new ArrayList<>();
}
