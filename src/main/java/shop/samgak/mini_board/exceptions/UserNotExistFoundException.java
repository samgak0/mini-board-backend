package shop.samgak.mini_board.exceptions;

public class UserNotExistFoundException extends RuntimeException {
    public UserNotExistFoundException(String username) {
        super("User not found: " + username);
    }
}
