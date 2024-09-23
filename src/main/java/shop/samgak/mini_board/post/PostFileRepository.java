<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/Repositories/PostFileRepository.java
package shop.samgak.mini_board.post.Repositories;
========
package shop.samgak.mini_board.post;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostFileRepository.java

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/Repositories/PostFileRepository.java
import shop.samgak.mini_board.post.entities.Post;
import shop.samgak.mini_board.post.entities.PostFile;

========
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostFileRepository.java
public interface PostFileRepository extends JpaRepository<PostFile, Long> {
    List<PostFile> findByPost(Post post);
}