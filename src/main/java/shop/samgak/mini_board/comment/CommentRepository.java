<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/repository/CommentRepository.java
package shop.samgak.mini_board.comment.repository;
========
package shop.samgak.mini_board.comment;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentRepository.java

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/repository/CommentRepository.java
import shop.samgak.mini_board.comment.entities.Comment;
import shop.samgak.mini_board.post.entities.Post;
========
import shop.samgak.mini_board.post.Post;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentRepository.java

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Post> findByUserId(Long userId);
}
