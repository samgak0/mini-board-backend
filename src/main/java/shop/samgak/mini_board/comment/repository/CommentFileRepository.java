package shop.samgak.mini_board.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.comment.entities.Comment;
import shop.samgak.mini_board.comment.entities.CommentFile;

public interface CommentFileRepository extends JpaRepository<CommentFile, Long> {
    List<CommentFile> findByComment(Comment comment);
}