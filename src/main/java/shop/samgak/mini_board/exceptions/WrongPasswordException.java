package shop.samgak.mini_board.exceptions;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException(String username) {
        super("Password does not match for user: " + username);
    }
}
