package shop.samgak.mini_board.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.entities.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Long countPostLikeByPostId(Long postId);
}