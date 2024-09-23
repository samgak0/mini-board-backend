<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/repository/CommentLikeRepository.java
package shop.samgak.mini_board.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.comment.entities.CommentLike;

========
package shop.samgak.mini_board.comment;

import org.springframework.data.jpa.repository.JpaRepository;

>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentLikeRepository.java
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Long countCommentLikeByCommentId(Long commentId);
}