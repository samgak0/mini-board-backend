package shop.samgak.mini_board.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.entities.Comment;
import shop.samgak.mini_board.entities.Like;
import shop.samgak.mini_board.entities.Post;

public interface LikeRepository extends JpaRepository<Like, Long> {
    long countByPost(Post post);

    long countByComment(Comment comment);
}