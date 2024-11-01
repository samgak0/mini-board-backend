package shop.samgak.mini_board.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보를 담는 Data Transfer Object (DTO) 클래스
 * 사용자 ID와 사용자명을 포함
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    /**
     * 사용자 ID
     */
    private Long id;
    /**
     * 사용자명
     */
    private String username;
}
