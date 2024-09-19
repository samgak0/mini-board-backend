package shop.samgak.mini_board.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.entities.Comment;
import shop.samgak.mini_board.entities.Post;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Post> findByUserId(Long userId);
}
