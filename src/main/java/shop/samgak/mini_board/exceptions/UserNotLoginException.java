package shop.samgak.mini_board.exceptions;

public class UserNotLoginException extends RuntimeException {
    public UserNotLoginException() {
        super("User is not Logged in");
    }
}
