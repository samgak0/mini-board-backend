package shop.samgak.mini_board.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.entities.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserId(Long userId);
}
