package shop.samgak.mini_board.utility;

/**
 * 인증이 필요한 요청에 대해 비인증 상태를 응답하는 클래스입니다.
 * 인증되지 않은 사용자가 접근할 수 없는 자원에 접근하려고 할 때 이 응답을 반환합니다.
 */
public class ApiUnauthrizationResponse extends ApiFailureResponse {

    /**
     * 기본 생성자: 인증이 필요한 경우의 에러 메시지와 실패 코드를 설정합니다.
     */
    public ApiUnauthrizationResponse() {
        super("Authentication is required");
    }
}
