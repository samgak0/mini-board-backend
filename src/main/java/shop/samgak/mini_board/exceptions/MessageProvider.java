package shop.samgak.mini_board.exceptions;

public class MessageProvider {

    static public String getMissingParameterMessage(String parameter) {
        return String.format("Missing required parameter: %s", parameter);
    }
}
