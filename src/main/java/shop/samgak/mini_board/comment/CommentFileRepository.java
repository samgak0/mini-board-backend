package shop.samgak.mini_board.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentFileRepository extends JpaRepository<CommentFile, Long> {
    List<CommentFile> findByComment(Comment comment);
}