package shop.samgak.mini_board.utility;

public class ApiUnauthrizationResponse extends ApiResponse {
    public static final String ERROR_AUTHENTICATION_REQUIRED = "Authentication is required";

    public ApiUnauthrizationResponse() {
        super(ERROR_AUTHENTICATION_REQUIRED, false);
    }
}
