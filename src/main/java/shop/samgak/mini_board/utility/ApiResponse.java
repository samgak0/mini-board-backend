package shop.samgak.mini_board.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse {

    public ApiResponse(String message, boolean success) {
        this(message, success ? Code.SUCCESS : Code.FAILURE);
    }

    public enum Code {
        SUCCESS("Success"),
        FAILURE("Failure"),
        USED("AlreadyUsed");

        private final String value;

        Code(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private final String message;
    private final Code code;
}
