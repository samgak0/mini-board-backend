package shop.samgak.mini_board.utility;

import lombok.Getter;

@Getter
public class ApiDataResponse extends ApiResponse {
    public ApiDataResponse(String message, Object data, boolean success) {
        super(message, success);
        this.data = data;
    }

    private final Object data;
}
