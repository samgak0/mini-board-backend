package shop.samgak.mini_board.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import shop.samgak.mini_board.exceptions.MessageProvider;
import shop.samgak.mini_board.exceptions.MissingParameterException;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.utility.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponse response = new ApiResponse(ex.getMessage(), false);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 필수 요청 파라미터가 없을 경우 예외 처리
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        String errorMessage = MessageProvider.getMissingParameterMessage(ex.getParameterName());
        return ResponseEntity.badRequest().body(new ApiResponse(errorMessage, false));
    }

    @ExceptionHandler(MissingParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingParameterException(MissingParameterException ex) {
        String errorMessage = MessageProvider.getMissingParameterMessage(ex.getParameterName());
        return ResponseEntity.badRequest().body(new ApiResponse(errorMessage, false));
    }
}
