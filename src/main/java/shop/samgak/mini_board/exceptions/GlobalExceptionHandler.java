package shop.samgak.mini_board.exceptions;

import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import shop.samgak.mini_board.utility.ApiExceptionResponse;
import shop.samgak.mini_board.utility.ApiResponse;
import shop.samgak.mini_board.utility.ApiUnauthrizationResponse;

/**
 * 공통 예외 처리 담당, 컨트롤러, 서비스에서 발생한 예외를 관리함
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // 요청 파라미터가 누락된 경우 처리
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingParams(MissingServletRequestParameterException e) {
        String errorMessage = String.format("Missing required parameter: %s", e.getParameterName());
        return ResponseEntity.badRequest().body(new ApiResponse(errorMessage, false));
    }

    // 메서드 인자 유효성 검사 실패 시 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder errorMessageBuilder = new StringBuilder();

        // 필드 오류를 정렬하고 메시지를 생성
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .sorted(Comparator.comparing(FieldError::getField)).toList();

        for (FieldError fieldError : fieldErrors) {
            errorMessageBuilder.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }

        String errorMessage = errorMessageBuilder.toString().trim();
        return ResponseEntity.badRequest().body(new ApiResponse(errorMessage, false));
    }

    // 사용자 정의 누락 파라미터 예외 처리
    @ExceptionHandler(MissingParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingParameterException(MissingParameterException e) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse(e.getMessage(), false));
    }

    // 로그인하지 않은 사용자 예외 처리
    @ExceptionHandler(UserNotLoginException.class)
    public ResponseEntity<ApiResponse> handleUserNotLoginException(UserNotLoginException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiUnauthrizationResponse());
    }

    // 리소스를 찾을 수 없는 경우 처리
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiExceptionResponse(e));
    }

    // 권한이 없는 경우 처리
    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ApiResponse> handleUnauthorizedActionException(UnauthorizedActionException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiExceptionResponse(e));
    }

    // 메시지를 읽을 수 없는 경우 처리 (예: JSON 파싱 오류)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(new ApiExceptionResponse(e));
    }

    // 메시지 변환 오류 처리
    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageConversionException(HttpMessageConversionException e) {
        return ResponseEntity.badRequest().body(new ApiExceptionResponse(e));
    }

    // 사용자를 찾을 수 없는 경우 처리
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotExistFoundException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(e.getMessage(), false));
    }

    // 비밀번호 오류 예외 처리
    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ApiResponse> handleWrongPasswordException(WrongPasswordException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(e.getMessage(), false));
    }

    // 서버 입출력 오류 예외 처리
    @ExceptionHandler(ServerIOException.class)
    public ResponseEntity<ApiExceptionResponse> handleServerIOException(ServerIOException e) {
        return ResponseEntity.internalServerError().body(new ApiExceptionResponse(e));
    }

    // 런타임 예외 처리 - 디버그 모드일 경우 스택 트레이스를 포함해 응답
    @ExceptionHandler(RuntimeException.class)
    @SuppressWarnings("CallToPrintStackTrace")
    public ResponseEntity<ApiResponse> handleRuntimeExcetiopn(RuntimeException e) {
        return ResponseEntity.internalServerError().body(new ApiExceptionResponse(e));
    }
}
