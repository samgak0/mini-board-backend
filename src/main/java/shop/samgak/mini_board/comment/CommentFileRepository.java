<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/repository/CommentFileRepository.java
package shop.samgak.mini_board.comment.repository;
========
package shop.samgak.mini_board.comment;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentFileRepository.java

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/repository/CommentFileRepository.java
import shop.samgak.mini_board.comment.entities.Comment;
import shop.samgak.mini_board.comment.entities.CommentFile;

========
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentFileRepository.java
public interface CommentFileRepository extends JpaRepository<CommentFile, Long> {
    List<CommentFile> findByComment(Comment comment);
}