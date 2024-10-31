package shop.samgak.mini_board.post.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.post.entities.PostLike;

// TODO : 전체적인 주석 추가
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Long countPostLikeByPostId(Long postId);
}