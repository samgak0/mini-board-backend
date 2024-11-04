package shop.samgak.mini_board.utility;

/**
 * API 실패 응답 처리
 */
public class ApiFailureResponse extends ApiResponse {

    public ApiFailureResponse(String message) {
        super(message, "FAILURE");
    }
}
