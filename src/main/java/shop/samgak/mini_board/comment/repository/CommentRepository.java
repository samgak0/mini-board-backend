package shop.samgak.mini_board.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.comment.entities.Comment;
import shop.samgak.mini_board.post.entities.Post;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Post> findByUserId(Long userId);
}
