package shop.samgak.mini_board.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.entities.Comment;
import shop.samgak.mini_board.entities.CommentFile;

public interface CommentFileRepository extends JpaRepository<CommentFile, Long> {
    List<CommentFile> findByComment(Comment comment);
}