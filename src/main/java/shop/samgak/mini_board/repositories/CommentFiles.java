package shop.samgak.mini_board.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.entities.Comment;

public interface CommentFiles extends JpaRepository<CommentFiles, Long> {
    List<Comment> findByPostId(Long postId);
}
