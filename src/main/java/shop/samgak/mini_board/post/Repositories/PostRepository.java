package shop.samgak.mini_board.post.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.post.entities.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserId(Long userId);

    List<Post> findTop10ByOrderByCreatedAtDesc();
}
