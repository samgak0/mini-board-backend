package shop.samgak.mini_board.utility;

import lombok.Getter;

/**
 * API 응답 처리를 위한 클래스
 */
@Getter
public class ApiResponse {

    private final String message;
    private final String code;

    public ApiResponse(String message, String code) {
        this.message = message;
        this.code = code;
    }
}
