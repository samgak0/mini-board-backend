package shop.samgak.mini_board.utility;

/**
 * ApiExceptionResponse 클래스: 예외가 발생했을 때 표준화된 응답을 생성하는 클래스
 */
public class ApiExceptionResponse extends ApiFailureResponse {

    /**
     * 예외 객체를 받아서 ApiResponse 형태로 생성하는 생성자
     * 
     * @param e 발생한 예외 객체
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public ApiExceptionResponse(Exception e) {
        // 부모 클래스인 ApiResponse의 생성자를 호출하여 예외 메시지와 실패 상태를 설정
        super(e.getMessage());
        e.printStackTrace();
    }
}
