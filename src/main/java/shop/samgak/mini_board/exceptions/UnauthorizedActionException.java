package shop.samgak.mini_board.exceptions;

/**
 * 사용자가 권한이 없는 행동을 할 때 발생하는 예외.
 * 
 * <p>
 * 이 예외는 사용자가 자신이 소유하지 않은 리소스에 대해 수정, 삭제 등의 작업을 시도할 때 발생합니다.
 * 예를 들어, 다른 사용자의 댓글을 삭제하려는 시도와 같은 상황에서 이 예외가 사용될 수 있습니다.
 * </p>
 */
public class UnauthorizedActionException extends RuntimeException {
    /**
     * 예외 메시지를 포함하는 생성자.
     * 
     * @param message 예외 원인 메시지
     */
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
