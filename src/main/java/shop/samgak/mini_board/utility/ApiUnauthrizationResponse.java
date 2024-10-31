package shop.samgak.mini_board.utility;

/**
 * 인증이 필요한 요청에 대해 비인증 상태를 응답하는 클래스입니다.
 * 인증되지 않은 사용자가 접근할 수 없는 자원에 접근하려고 할 때 이 응답을 반환합니다.
 */
public class ApiUnauthrizationResponse extends ApiResponse {
    // 인증이 필요한 경우에 표시할 에러 메시지 상수
    public static final String ERROR_AUTHENTICATION_REQUIRED = "Authentication is required";

    /**
     * 기본 생성자: 인증이 필요한 경우의 에러 메시지와 실패 코드를 설정합니다.
     */
    public ApiUnauthrizationResponse() {
        super(ERROR_AUTHENTICATION_REQUIRED, false);
    }
}
