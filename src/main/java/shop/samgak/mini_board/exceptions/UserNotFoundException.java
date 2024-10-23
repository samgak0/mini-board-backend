package shop.samgak.mini_board.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User Not Found");
    }
}
