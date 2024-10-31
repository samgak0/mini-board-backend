package shop.samgak.mini_board.utility;

import lombok.Getter;

// API 응답 처리를 위한 클래스
@Getter
public class ApiResponse {

    // 응답 메시지
    private final String message;
    // 응답 코드
    private final Code code;

    // 응답 메시지와 코드로 생성자 정의
    public ApiResponse(String message, Code code) {
        this.message = message;
        this.code = code;
    }

    // 성공 여부에 따라 메시지와 코드 설정하는 생성자 정의
    public ApiResponse(String message, boolean success) {
        this(message, success ? Code.SUCCESS : Code.FAILURE);
    }

    // 응답 코드 종류를 나타내는 열거형(enum) 정의
    public enum Code {
        SUCCESS("Success"), // 요청이 성공적으로 처리되었을 때 사용
        FAILURE("Failure"), // 요청 처리에 실패했을 때 사용
        USED("AlreadyUsed"); // 이미 사용 중인 자원에 대한 요청일 때 사용

        private final String value;

        // 응답 코드 값을 설정하는 생성자
        Code(String value) {
            this.value = value;
        }

        // 응답 코드 값을 반환하는 메서드
        public String getValue() {
            return value;
        }
    }
}
