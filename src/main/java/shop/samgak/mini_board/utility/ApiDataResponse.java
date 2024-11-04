package shop.samgak.mini_board.utility;

import lombok.Getter;

/**
 * API 응답에 추가적인 데이터를 포함시키기 위해 사용됩니다.
 * 기본 ApiResponse와는 다르게, 데이터 필드를 추가하여 클라이언트에게 정보를 전달합니다.
 */
@Getter
public class ApiDataResponse extends ApiSuccessResponse {
    // 클라이언트에게 반환할 추가적인 데이터 객체
    private final Object data;

    // 생성자: 메시지, 데이터, 성공 여부를 인자로 받아 ApiDataResponse 객체를 생성합니다.
    public ApiDataResponse(String message, Object data) {
        super(message);
        this.data = data; // 추가적인 데이터 필드 초기화
    }
}
