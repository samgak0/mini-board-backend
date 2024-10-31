package shop.samgak.mini_board.post.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.post.entities.Post;

/**
 * 게시물 관련 데이터 접근을 처리하는 레포지토리 인터페이스
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 삭제되지 않은 특정 사용자가 작성한 게시물을 찾는 메서드
     * isDeleted 필드가 false인 게시물만 조회합니다. 논리적으로 삭제되지 않은 게시물만 반환됩니다.
     * 
     * @param userId 사용자 ID
     * @return 삭제되지 않은 게시물의 Optional 객체
     * 
     */
    Optional<Post> findByUserIdAndIsDeletedFalse(Long userId);

    /**
     * 삭제되지 않은 특정 게시물을 ID로 찾는 메서드
     * isDeleted 필드가 false인 게시물만 조회합니다. 논리적으로 삭제되지 않은 게시물만 반환됩니다.
     * 
     * @param postId 게시물 ID
     * @return 삭제되지 않은 게시물의 Optional 객체
     * 
     */
    Optional<Post> findByIdAndIsDeletedFalse(Long postId);

    /**
     * 삭제되지 않은 최신 게시물 상위 10개를 가져오는 메서드
     * isDeleted 필드가 false인 게시물만 조회하며, 생성 일시(createdAt)를 기준으로 내림차순 정렬하여 상위
     * 10개의 게시물을 반환합니다.
     * 
     * @return 삭제되지 않은 최신 게시물 목록
     * 
     */
    List<Post> findTop10ByIsDeletedFalseOrderByCreatedAtDesc();
}
