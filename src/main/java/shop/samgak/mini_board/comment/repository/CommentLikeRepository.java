package shop.samgak.mini_board.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.comment.entities.CommentLike;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Long countCommentLikeByCommentId(Long commentId);
}