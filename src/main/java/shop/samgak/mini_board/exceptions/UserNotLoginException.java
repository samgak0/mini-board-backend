package shop.samgak.mini_board.exceptions;

/**
 * 사용자 인증 단계에서 사용자가 로그인 하지 않았을 경우 사용하는 예외
 */
public class UserNotLoginException extends RuntimeException {
    // 기본 생성자: 로그인 하지 않았다는 메시지로 예외를 발생시킴
    public UserNotLoginException() {
        super("User is not Logged in");
    }
}
