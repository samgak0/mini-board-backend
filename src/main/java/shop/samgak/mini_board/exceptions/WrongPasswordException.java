package shop.samgak.mini_board.exceptions;

/**
 * 사용자 인증 단계에서 사용자 비밀번호가 일치 하지 않았을 경우 사용하는 예외
 */
public class WrongPasswordException extends RuntimeException {
    /**
     * 특정 사용자의 비밀번호가 일치하지 않을 때 예외를 생성합니다.
     * 
     * @param username 비밀번호가 일치하지 않는 사용자 이름
     */
    public WrongPasswordException(String username) {
        super("Password does not match for user: " + username);
    }
}
