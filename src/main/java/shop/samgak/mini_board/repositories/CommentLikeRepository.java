package shop.samgak.mini_board.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.entities.CommentLike;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Long countCommentLikeByCommentId(Long commentId);
}