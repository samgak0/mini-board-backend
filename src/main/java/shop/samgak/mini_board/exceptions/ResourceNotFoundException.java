package shop.samgak.mini_board.exceptions;

/**
 * DB 요소를 찾지 못할 때 사용하는 예외
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
