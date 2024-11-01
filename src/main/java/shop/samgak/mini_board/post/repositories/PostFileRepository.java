package shop.samgak.mini_board.post.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.post.entities.PostFile;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {

    /**
     * 특정 게시글에 속하는 논리삭제(isDeleted = false)되지 않는 파일 목록 반환
     * 
     * @param postId 게시글 ID
     * @return 삭제되지 않은 파일 목록
     */
    List<PostFile> findByPostIdAndIsDeletedFalse(Long postId);

    /**
     * 해당 postFileId 이면서 논리삭제(isDeleted = false)되지 않는 항목을 조회
     * 
     * @param postFileId 파일 ID
     * @return 삭제되지 않은 파일의 Optional 객체
     */
    Optional<PostFile> findByIdAndIsDeletedFalse(Long postFileId);

    /**
     * 해당 PostFileId와 PostId이면서 논리삭제(isDeleted = false)되지 않는 항목을 조회
     * 
     * @param postFileId 파일 ID
     * @param postId     게시글 ID
     * @return 삭제되지 않은 파일의 Optional 객체
     * 
     * 
     */
    Optional<PostFile> findByIdAndPostIdAndIsDeletedFalse(Long postFileId, Long postId);
}
