package shop.samgak.mini_board.comment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import shop.samgak.mini_board.comment.entities.Comment;

/**
 * 댓글 데이터를 관리하는 JPA 저장소입
 * 데이터베이스에 저장된 댓글 정보를 CRUD 작업을 통해 관리하는 역할을 수행
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * 특정 게시물에 속한 모든 댓글을 조회하는 메서드
     * 
     * @param postId 조회하고자 하는 게시물의 ID
     * @return 해당 게시물에 속한 댓글 리스트
     */
    List<Comment> findByPostId(Long postId);

    Optional<Comment> findByIdAndPostId(Long commentId, Long postId);
}
