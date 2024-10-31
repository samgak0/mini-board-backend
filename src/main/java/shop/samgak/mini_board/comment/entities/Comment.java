package shop.samgak.mini_board.comment.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import shop.samgak.mini_board.post.entities.Post;
import shop.samgak.mini_board.user.entities.User;

/**
 * 댓글 엔티티 클래스
 * 각 댓글은 사용자와 게시물에 속하며, 상위 댓글을 가질 수 있음
 */
@Entity
@Table(name = "comments")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_seq")
    @SequenceGenerator(name = "comments_seq", sequenceName = "COMMENTS_SEQ", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 댓글 작성자 정보. 댓글은 특정 사용자에 의해 작성
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    /**
     * 댓글이 속한 게시물 정보. 각 댓글은 특정 게시물에 속함
     */
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false, updatable = false)
    private Post post;

    /**
     * 상위 댓글 정보. 대댓글인 경우 상위 댓글을 참조할 수 있음. 그렇지 않으면 null
     */
    @ManyToOne
    @JoinColumn(name = "parent_comment_id", nullable = true, updatable = false)
    private Comment parentComment;

    /**
     * 댓글 내용. 텍스트 형태로 저장됨
     */
    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    /**
     * 댓글 생성 시간. 기본적으로 현재 시간으로 설정
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    /**
     * 댓글 수정 시간. 댓글이 수정될 때마다 갱신
     */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();
}
