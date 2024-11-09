package shop.samgak.mini_board.exceptions;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.core.JsonProcessingException;

import shop.samgak.mini_board.utility.ApiExceptionResponse;
import shop.samgak.mini_board.utility.ApiFailureResponse;
import shop.samgak.mini_board.utility.ApiResponse;
import shop.samgak.mini_board.utility.ApiUnauthrizationResponse;

/**
 * GlobalExceptionHandler는 어플리케이션 전반에 발생하는 예외를 처리하여
 * 일관된 응답을 제공함
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 요청 파라미터가 누락된 경우 처리
     * 
     * @param e 누락된 요청 파라미터 예외
     * @return 요청 파라미터 누락 메시지와 HTTP 400 상태 코드
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingParams(MissingServletRequestParameterException e) {
        String errorMessage = String.format("Missing required parameter: %s", e.getParameterName());
        return ResponseEntity.badRequest().body(new ApiFailureResponse(errorMessage));
    }

    /**
     * 사용자에게서 온 타입과 요청 파라미터 타입이 불일치한 경우
     * ex) 숫자(Long) 자리에 문자(String)가 들어감
     * 
     * @param e 불일치 요청 파라미터 예외
     * @return 요청 파라미터 누락 메시지와 HTTP 400 상태 코드
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        String errorMessage = String.format("An invalid parameter was given: %s", e.getParameter().getParameterName());
        return ResponseEntity.badRequest().body(new ApiFailureResponse(errorMessage));
    }

    /**
     * 사용자에게서 온 타입과 요청 파라미터 타입이 불일치한 경우
     * ex) 숫자(Long) 자리에 문자(String)가 들어감
     * 
     * @param e 불일치 요청 파라미터 예외
     * @return 요청 파라미터 누락 메시지와 HTTP 400 상태 코드
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiResponse> handleMissingPathVariableException(
            MissingPathVariableException e) {
        String errorMessage = String.format("Path variable was not provided: %s", e.getParameter().getParameterName());
        return ResponseEntity.badRequest().body(new ApiFailureResponse(errorMessage));
    }

    /**
     * 메서드 인자 유효성 검사 실패 시 처리
     * 
     * @param e 유효하지 않은 메서드 인자 예외
     * @return 필드 오류 메시지와 HTTP 400 상태 코드
     */
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
        return ResponseEntity.badRequest().body(new ApiFailureResponse(errorMessage));
    }

    /**
     * 사용자 정의 누락 파라미터 예외 처리
     * 
     * @param e 사용자 정의 누락 파라미터 예외
     * @return 오류 메시지와 HTTP 400 상태 코드
     */
    @ExceptionHandler(MissingParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingParameterException(MissingParameterException e) {
        return ResponseEntity.badRequest()
                .body(new ApiFailureResponse(e.getMessage()));
    }

    /**
     * 미 지원 컨탠츠 타입(application/json 이외) 예외 처리
     * 
     * @param e 사용자 정의 누락 파라미터 예외
     * @return 오류 메시지와 HTTP 400 상태 코드
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return ResponseEntity.badRequest()
                .body(new ApiFailureResponse(e.getMessage()));
    }

    /**
     * 로그인하지 않은 사용자 예외 처리
     * 
     * @param e 로그인되지 않은 사용자 예외
     * @return 인증 필요 메시지와 HTTP 401 상태 코드
     */
    @ExceptionHandler(UserNotLoginException.class)
    public ResponseEntity<ApiResponse> handleUserNotLoginException(UserNotLoginException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiUnauthrizationResponse());
    }

    /**
     * 리소스를 찾을 수 없는 경우 처리
     * 
     * @param e 리소스 없음 예외
     * @return 리소스 없음 메시지와 HTTP 404 상태 코드
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiExceptionResponse(e));
    }

    /**
     * 권한이 없는 경우 처리
     * 
     * @param e 권한 없음 예외
     * @return 권한 없음 메시지와 HTTP 403 상태 코드
     */
    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ApiResponse> handleUnauthorizedActionException(UnauthorizedActionException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiExceptionResponse(e));
    }

    /**
     * 메시지를 읽을 수 없는 경우 처리 (예: JSON 파싱 오류)
     * 
     * @param e 메시지 읽기 불가 예외
     * @return 오류 메시지와 HTTP 400 상태 코드
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(new ApiFailureResponse("Required request body is missing"));
    }

    /**
     * 메시지 변환 오류 처리
     * 
     * @param e 메시지 변환 예외
     * @return 변환 오류 메시지와 HTTP 400 상태 코드
     */
    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageConversionException(HttpMessageConversionException e) {
        return ResponseEntity.badRequest().body(new ApiExceptionResponse(e));
    }

    /**
     * 사용자를 찾을 수 없는 경우 처리
     * 
     * @param e 사용자 없음 예외
     * @return 사용자 없음 메시지와 HTTP 401 상태 코드
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotExistFoundException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiFailureResponse(e.getMessage()));
    }

    /**
     * 비밀번호 오류 예외 처리
     * 
     * @param e 비밀번호 오류 예외
     * @return 비밀번호 오류 메시지와 HTTP 401 상태 코드
     */
    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ApiResponse> handleWrongPasswordException(WrongPasswordException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiFailureResponse(e.getMessage()));
    }

    /**
     * 서버 입출력 오류 예외 처리
     * 
     * @param e 서버 입출력 오류 예외
     * @return 서버 오류 메시지와 HTTP 500 상태 코드
     */
    @ExceptionHandler(ServerIOException.class)
    public ResponseEntity<ApiExceptionResponse> handleServerIOException(ServerIOException e) {
        return ResponseEntity.internalServerError().body(new ApiExceptionResponse(e));
    }

    /**
     * 입출력 예외 처리
     * 
     * @param e 입출력 예외
     * @return 오류 메시지와 HTTP 500 상태 코드
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiExceptionResponse> handleIOException(IOException e) {
        return ResponseEntity.internalServerError().body(new ApiExceptionResponse(e));
    }

    /**
     * JSON 처리 예외 처리
     * 
     * @param e JSON 처리 예외
     * @return 서버 오류 메시지와 HTTP 500 상태 코드
     */
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ApiExceptionResponse> handleJsonProcessingException(JsonProcessingException e) {
        return ResponseEntity.internalServerError().body(new ApiExceptionResponse(e));
    }

    /**
     * 
     * @param e 런타임 예외
     * @return 예외 메시지와 HTTP 500 상태 코드
     */
    @ExceptionHandler(RuntimeException.class)
    @SuppressWarnings("CallToPrintStackTrace")
    public ResponseEntity<ApiResponse> handleRuntimeExcetiopn(RuntimeException e) {
        return ResponseEntity.internalServerError().body(new ApiExceptionResponse(e));
    }
}
