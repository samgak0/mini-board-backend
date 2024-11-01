package shop.samgak.mini_board.post.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.post.entities.PostFile;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {

    /**
     * 지정 게시글에 속하는 목록 반환
     * 
     * @param postId 게시글 ID
     * @return 삭제되지 않은 파일 목록
     */
    List<PostFile> findByPostId(Long postId);

    /**
     * 해당 PostFileId와 PostId인 항목을 실제 항목 조회
     * 
     * @param postFileId 파일 ID
     * @param postId     게시글 ID
     * @return 파일 Optional 객체
     */
    Optional<PostFile> findByIdAndPostId(Long postFileId, Long postId);
}
