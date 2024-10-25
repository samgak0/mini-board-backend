package shop.samgak.mini_board.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import shop.samgak.mini_board.utility.ApiDataResponse;
import shop.samgak.mini_board.utility.ApiExceptionResponse;
import shop.samgak.mini_board.utility.ApiResponse;
import shop.samgak.mini_board.utility.ApiUnauthrizationResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.debug}")
    private boolean debugMode;

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingParams(MissingServletRequestParameterException e) {
        String errorMessage = MessageProvider.getMissingParameterMessage(e.getParameterName());
        return ResponseEntity.badRequest().body(new ApiResponse(errorMessage, false));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder errorMessageBuilder = new StringBuilder();

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

    @ExceptionHandler(MissingParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingParameterException(MissingParameterException e) {
        String errorMessage = MessageProvider.getMissingParameterMessage(e.getParameterName());
        return ResponseEntity.badRequest().body(new ApiResponse(errorMessage, false));
    }

    @ExceptionHandler(UserNotLoginException.class)
    public ResponseEntity<ApiResponse> handleUserNotLoginException(UserNotLoginException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiUnauthrizationResponse());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), false));
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ApiResponse> handleUnauthorizedActionException(UnauthorizedActionException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiExceptionResponse(e));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(new ApiExceptionResponse(e));
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageConversionException(HttpMessageConversionException e) {
        return ResponseEntity.badRequest().body(new ApiDataResponse("", e.getMessage(), false));
    }

    @ExceptionHandler(UserNotExistFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotExistFoundException(UserNotExistFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(e.getMessage(), false));
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ResponseEntity<ApiResponse> handleWrongPasswordException(WrongPasswordException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(e.getMessage(), false));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeExcetiopn(RuntimeException e) {

        if (debugMode) {
            String eol = System.getProperty("line.separator");
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.write(e.getMessage() + eol);
            e.printStackTrace(pw);
            return ResponseEntity.internalServerError().body(new ApiResponse(sw.toString(), false));
        } else {
            return ResponseEntity.internalServerError().body(new ApiResponse(e.getMessage(), false));
        }
    }
}
