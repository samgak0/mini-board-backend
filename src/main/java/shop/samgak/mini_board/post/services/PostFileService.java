package shop.samgak.mini_board.post.services;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import shop.samgak.mini_board.post.dto.PostFileDTO;
import shop.samgak.mini_board.user.dto.UserDTO;

/**
 * 게시물 파일 관련 기능을 제공하는 서비스 인터페이스
 */
public interface PostFileService {
    /**
     * 특정 게시물에 첨부된 파일 목록을 가져오는 메서드
     * 
     * @param postId 게시물 ID
     * @return 해당 게시물에 첨부된 파일 목록
     */
    List<PostFileDTO> getItemByPost(Long postId);

    /**
     * 특정 게시물에 속한 특정 파일을 가져오는 메서드
     * 
     * @param postFileId 파일 ID
     * @param postId     게시물 ID
     * @return 해당 파일의 정보
     */
    PostFileDTO getItem(Long postFileId, Long postId);

    /**
     * 특정 게시물 파일의 조회수를 증가시키는 메서드 (세션을 사용하여 중복 조회 방지)
     * 
     * @param postId  게시물 ID
     * @param session 현재 세션 객체
     */
    void increaseViewCount(Long postId, HttpSession session);

    /**
     * 특정 게시물 파일의 조회수를 증가시키는 메서드
     * 
     * @param postId 게시물 ID
     */
    void increaseViewCount(Long postId);

    /**
     * 특정 파일을 삭제하는 메서드
     * 
     * @param postFileId 파일 ID
     * @param userDTO    현재 사용자 정보
     */
    void deleteFile(Long postFileId, UserDTO userDTO);

    /**
     * 게시물 파일 정보를 데이터베이스에 저장하는 메서드
     * 
     * @param postId           게시물 ID
     * @param originalFileName 원본 파일 이름
     * @param filename         서버에 저장된 파일 이름
     * @param contentType      파일의 MIME 타입
     * @param fileSize         파일 크기
     * @param userDTO          현재 사용자 정보
     * @return 저장된 파일의 정보
     */
    PostFileDTO writePostFileInfo(Long postId, String originalFileName, String filename, String contentType,
            long fileSize, UserDTO userDTO);
}
