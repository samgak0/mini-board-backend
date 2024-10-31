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
    private Long id; // 파일의 고유 ID
    private String originalName; // 파일의 원래 이름
    private String fileName; // 서버에 저장된 파일 이름
    private String contentType; // 파일의 MIME 타입 (예: image/png, application/pdf 등)
    private Long viewCount; // 파일 조회수
    private Long fileSize; // 파일 크기 (바이트 단위)
    private Boolean isDeleted; // 파일 삭제 여부 (논리적 삭제 처리용)
    private Instant createdAt; // 파일이 생성된 일시
}
