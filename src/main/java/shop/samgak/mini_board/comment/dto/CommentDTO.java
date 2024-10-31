package shop.samgak.mini_board.comment.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.user.dto.UserDTO;

/**
 * 댓글 정보를 담는 DTO 클래스
 * 댓글의 기본적인 정보(작성자, 게시물, 내용 등)를 포함
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    /**
     * 댓글 ID (고유 식별자)
     */
    private Long id;

    /**
     * 댓글 작성자 정보
     */
    private UserDTO user;

    /**
     * 댓글이 달린 게시물 정보
     */
    private PostDTO post;

    /**
     * 상위 댓글 정보 (대댓글일 경우)
     * JsonIgnore를 통해 직렬화 시 제외하여 순환 참조 문제를 방지
     */
    @JsonIgnore
    private CommentDTO parentComment;

    /**
     * 댓글 내용
     */
    private String content;

    /**
     * 댓글 생성 시간
     */
    private Instant createdAt;

    /**
     * 댓글 수정 시간
     */
    private Instant updatedAt;
}
