package shop.samgak.mini_board.exceptions;

public class MissingParameterException extends RuntimeException {
    private final String parameterName;

    public MissingParameterException(String parameterName) {
        super(MessageProvider.getMissingParameterMessage(parameterName));
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }
}
