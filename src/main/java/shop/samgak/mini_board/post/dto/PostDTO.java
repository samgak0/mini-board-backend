package shop.samgak.mini_board.post.dto;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.samgak.mini_board.post.entities.PostFile;
import shop.samgak.mini_board.user.dto.UserDTO;

/**
 * 게시글 정보를 담고 있는 DTO 클래스.
 * 클라이언트와 서버 간 데이터 전송 시 사용되는 객체로, 게시글의 주요 정보를 포함합니다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    /**
     * 게시글 ID
     */
    private Long id;
    /**
     * 게시글 작성자 정보
     */
    private UserDTO user;
    /**
     * 게시글 제목
     */
    private String title;
    /**
     * 게시글 내용
     */
    private String content;
    /**
     * 게시글 조회수
     */
    private Long viewCount;
    /**
     * 게시글 생성 일시
     */
    private Instant createdAt;
    /**
     * 게시글 수정 일시
     */
    private Instant updatedAt;
    /**
     * 게시글 삭제 일시 (논리적 삭제 처리)
     */
    private Instant deletedAt;
    /**
     * 게시글에 첨부된 파일 목록
     */
    private List<PostFile> postFiles;
}
