<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/Repositories/PostLikeRepository.java
package shop.samgak.mini_board.post.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.post.entities.PostLike;

========
package shop.samgak.mini_board.post;

import org.springframework.data.jpa.repository.JpaRepository;

>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostLikeRepository.java
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Long countPostLikeByPostId(Long postId);
}