package shop.samgak.mini_board.post.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import shop.samgak.mini_board.post.entities.Post;

/**
 * 게시물 데이터 접근을 처리하는 레포지토리 인터페이스
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 삭제되지 않은 특정 게시물을 ID로 조회
     * 
     * @param postId 게시물 ID
     * @return 게시물의 Optional 객체
     */
    Optional<Post> findByIdAndDeletedAtIsNull(Long postId);

    /**
     * 삭제되지 않은 최신 게시물 상위 10개 조회
     * 삭제되지 않은 게시물을 생성 일시(createdAt) 기준으로 내림차순 정렬하여 반환합니다.
     * 
     * @return 삭제되지 않은 최신 게시물 목록
     */
    List<Post> findTop10ByDeletedAtIsNullOrderByCreatedAtDesc();
}
