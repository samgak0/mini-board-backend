<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/Repositories/PostRepository.java
package shop.samgak.mini_board.post.Repositories;
========
package shop.samgak.mini_board.post;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostRepository.java

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/Repositories/PostRepository.java
import shop.samgak.mini_board.post.entities.Post;

========
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostRepository.java
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserId(Long userId);
}
