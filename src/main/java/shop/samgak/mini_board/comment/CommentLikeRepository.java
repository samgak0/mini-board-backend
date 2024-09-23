package shop.samgak.mini_board.comment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Long countCommentLikeByCommentId(Long commentId);
}