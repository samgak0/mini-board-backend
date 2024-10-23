package shop.samgak.mini_board.utility;

public class ApiExceptionResponse extends ApiResponse {
    public ApiExceptionResponse(Exception e) {
        super(e.getMessage(), false);
    }
}
