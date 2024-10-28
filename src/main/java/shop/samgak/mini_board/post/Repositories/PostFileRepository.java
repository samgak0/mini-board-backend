package shop.samgak.mini_board.post.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.post.entities.PostFile;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {
    List<PostFile> findByPostId(Long postId);

    Optional<PostFile> findByIdAndPostId(Long postFileId, Long postId);
}