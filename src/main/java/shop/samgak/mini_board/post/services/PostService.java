package shop.samgak.mini_board.post.services;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.user.dto.UserDTO;

/**
 * 게시물 관련 기능을 제공하는 서비스 인터페이스
 */
public interface PostService {
    /**
     * 최신 게시물 상위 10개를 가져오는 메서드
     * 
     * @return 최신 게시물 상위 10개의 목록
     */
    List<PostDTO> getTop10();

    /**
     * 특정 게시물의 조회수를 증가시키는 메서드 (세션을 사용하여 중복 조회 방지)
     * 
     * @param postId  게시물 ID
     * @param session 현재 세션 객체
     */
    boolean increaseViewCount(Long postId, HttpSession session);

    /**
     * 특정 ID의 게시물을 가져오는 메서드
     * 
     * @param id 게시물 ID
     * @return 해당 게시물의 정보
     */
    PostDTO getPostById(Long id);

    /**
     * 새로운 게시물을 생성하는 메서드
     * 
     * @param title   게시물 제목
     * @param content 게시물 내용
     * @param userDTO 작성자 정보
     * @return 생성된 게시물의 ID
     */
    Long create(String title, String content, UserDTO userDTO);

    /**
     * 특정 게시물을 수정하는 메서드
     * 
     * @param id      수정할 게시물 ID
     * @param title   수정할 제목
     * @param content 수정할 내용
     * @param userDTO 수정 요청 사용자 정보
     * @return 게시글의 업데이트 여부
     */
    boolean update(Long id, String title, String content, UserDTO userDTO);

    /**
     * 특정 게시물을 삭제하는 메서드
     * 
     * @param postId  삭제할 게시물 ID
     * @param userDTO 삭제 요청 사용자 정보
     */
    void delete(Long postId, UserDTO userDTO);

    /**
     * 특정 ID의 게시물이 존재하는지 확인하는 메서드
     * 
     * @param id 게시물 ID
     * @return 게시물 존재 여부
     */
    boolean existsById(Long id);
}
