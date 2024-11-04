package shop.samgak.mini_board.utility;

/**
 * API 성공 응답 처리
 */
public class ApiSuccessResponse extends ApiResponse {

    public ApiSuccessResponse(String message) {
        super(message, "SUCCESS");
    }

    public ApiSuccessResponse() {
        super("success", "SUCCESS");
    }
}
