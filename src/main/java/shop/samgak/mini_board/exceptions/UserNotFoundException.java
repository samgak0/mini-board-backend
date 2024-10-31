package shop.samgak.mini_board.exceptions;

/**
 * 사용자 인증 단계에서 사용자를 찾을 수 없는 경우 발생하는 예외 클래스
 * 
 * @param username 찾지 못한 사용자 이름
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("User not found: " + username);
    }
}
