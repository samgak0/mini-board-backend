package shop.samgak.mini_board.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.entities.Post;
import shop.samgak.mini_board.entities.PostFile;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {
    List<PostFile> findByPost(Post post);
}