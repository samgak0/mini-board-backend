package shop.samgak.mini_board.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse {
    private final String message;
    private final boolean success;
}
