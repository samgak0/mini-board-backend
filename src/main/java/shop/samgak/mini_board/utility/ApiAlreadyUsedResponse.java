package shop.samgak.mini_board.utility;

import lombok.Getter;

/**
 * API 응답 처리를 위한 클래스
 */
@Getter
public class ApiAlreadyUsedResponse extends ApiResponse {

    public ApiAlreadyUsedResponse(String message) {
        super(message, "ALREADYUSED");
    }
}
