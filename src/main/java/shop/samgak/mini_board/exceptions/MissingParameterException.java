package shop.samgak.mini_board.exceptions;

/**
 * 필수 매개변수가 누락된 경우 발생하는 예외
 */
public class MissingParameterException extends RuntimeException {

    public MissingParameterException(String parameterName) {
        super(String.format("Missing required parameter: %s", parameterName));
    }
}