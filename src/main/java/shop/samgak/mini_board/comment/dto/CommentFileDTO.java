package shop.samgak.mini_board.comment.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 댓글에 첨부된 파일의 정보를 담고 있는 데이터 전송 객체
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentFileDTO {
    /**
     * 파일의 고유 식별자
     */
    private Long id;

    /**
     * 파일이 첨부된 댓글 객체
     */
    private CommentDTO comment;

    /**
     * 파일의 원래 이름
     */
    private String originalName;

    /**
     * 파일이 저장된 경로
     */
    private String filePath;

    /**
     * 파일의 크기 (바이트 단위)
     */
    private Long fileSize;

    /**
     * 파일의 생성 시간
     */
    private Instant createdAt;
}
