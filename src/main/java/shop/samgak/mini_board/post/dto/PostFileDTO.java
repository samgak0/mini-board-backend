package shop.samgak.mini_board.post.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 게시글에 첨부된 파일 정보를 담고 있는 DTO 클래스.
 * 파일의 기본 정보와 메타데이터를 포함합니다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostFileDTO {
    /**
     * 파일의 고유 ID
     */
    private Long id;

    /**
     * 파일의 원래 이름
     */
    private String originalName;

    /**
     * 서버에 저장된 파일 이름
     */
    private String fileName;

    /**
     * 파일의 MIME 타입
     * 예: image/png, application/pdf 등
     */
    private String contentType;

    /**
     * 파일 조회수
     */
    private Long viewCount;

    /**
     * 파일 크기 (바이트 단위)
     */
    private Long fileSize;

    /**
     * 파일이 생성된 일시
     */
    private Instant createdAt;
}
