package shop.samgak.mini_board.user.controllers;

import java.net.URI;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.user.services.UserService;
import shop.samgak.mini_board.utility.ApiDataResponse;
import shop.samgak.mini_board.utility.ApiExceptionResponse;
import shop.samgak.mini_board.utility.ApiResponse;
import shop.samgak.mini_board.utility.AuthUtils;

/**
 * 사용자 관련 API 요청을 처리하는 컨트롤러 정의
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users/")
public class UserController {

    // 비밀번호 패턴 정규식 정의 (숫자, 소문자, 대문자, 특수문자를 포함하고 최소 8자 이상)
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$";

    // 세션에 사용자 이름 및 이메일을 저장하는 키 상수 정의
    public static final String SESSION_CHECKED_USER = "checked_user_name";
    public static final String SESSION_CHECKED_EMAIL = "checked_email";

    // 에러 메시지 상수 정의
    public static final String ERROR = "Error occurred";
    public static final String ERROR_INVALID_EMAIL_FORMAT = "Invalid email format";
    public static final String ERROR_USERNAME_CHECK_NOT_PERFORMED = "Username availability check not performed";
    public static final String ERROR_EMAIL_CHECK_NOT_PERFORMED = "Email availability check not performed";
    public static final String ERROR_USERNAME_ALREADY_USED = "Username is already in use";
    public static final String ERROR_EMAIL_ALREADY_USED = "Email is already in use";
    public static final String ERROR_USERNAME_MISMATCH = "Username does not match the checked username";
    public static final String ERROR_EMAIL_MISMATCH = "Email does not match the checked email";

    // 성공 메시지 상수 정의
    public static final String MESSAGE_PASSWORD_CHANGE_SUCCESSFUL = "Password change successful";
    public static final String MESSAGE_REGISTER_SUCCESSFUL = "Registration successful";
    public static final String MESSAGE_EMAIL_AVAILABLE = "Email is available";
    public static final String MESSAGE_USERNAME_AVAILABLE = "Username is available";
    public static final String MESSAGE_LOGIN_STATUS = "Login status";

    // 사용자 서비스 객체 주입
    final UserService userService;

    /**
     * 사용자 이름 사용 가능 여부를 체크하는 엔드포인트
     * 
     * @param request 사용자 이름 체크 요청 객체
     * @param session 현재 세션 객체
     * @return 사용자 이름 사용 가능 여부에 대한 응답
     */
    @PostMapping("check/username")
    public ResponseEntity<ApiResponse> checkUsername(@RequestBody @Valid CheckUsernameRequest request,
            HttpSession session) {

        log.info("사용자 이름 체크 요청: [{}]", request.username);
        // 사용자 이름 존재 여부 확인
        boolean isExistUserName = userService.existUsername(request.username);
        if (!isExistUserName) {
            // 사용자 이름이 존재하지 않으면 세션에 저장하고 사용 가능 메시지 반환
            session.setAttribute(SESSION_CHECKED_USER, request.username);
            log.info("사용자 이름 사용 가능: [{}]", request.username);
            return ResponseEntity.ok().body(new ApiResponse(MESSAGE_USERNAME_AVAILABLE, true));
        } else {
            // 사용자 이름이 이미 존재하면 CONFLICT 상태와 함께 에러 메시지 반환
            log.warn("사용자 이름 이미 사용 중: [{}]", request.username);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(ERROR_USERNAME_ALREADY_USED, ApiResponse.Code.USED));
        }
    }

    /**
     * 이메일 사용 가능 여부를 체크하는 엔드포인트
     * 
     * @param request 이메일 체크 요청 객체
     * @param session 현재 세션 객체
     * @return 이메일 사용 가능 여부에 대한 응답
     */
    @PostMapping("check/email")
    public ResponseEntity<ApiResponse> checkEmail(@RequestBody @Valid CheckEmailRequest request,
            HttpSession session) {
        log.info("이메일 체크 요청: [{}]", request.email);
        // 이메일 형식 유효성 검사
        EmailValidator validator = EmailValidator.getInstance();
        if (!validator.isValid(request.email)) {
            // 유효하지 않은 이메일 형식일 경우 BAD_REQUEST 상태와 함께 에러 메시지 반환
            log.warn("잘못된 이메일 형식: [{}]", request.email);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_INVALID_EMAIL_FORMAT, false));
        }

        // 이메일 존재 여부 확인
        boolean isExistEmail = userService.existEmail(request.email);
        if (!isExistEmail) {
            // 이메일이 존재하지 않으면 세션에 저장하고 사용 가능 메시지 반환
            session.setAttribute(SESSION_CHECKED_EMAIL, request.email);
            log.info("이메일 사용 가능: [{}]", request.email);
            return ResponseEntity.ok().body(new ApiResponse(MESSAGE_EMAIL_AVAILABLE, true));
        } else {
            // 이메일이 이미 존재하면 CONFLICT 상태와 함께 에러 메시지 반환
            log.warn("이메일 이미 사용 중: [{}]", request.email);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(ERROR_EMAIL_ALREADY_USED, ApiResponse.Code.USED));
        }
    }

    /**
     * 비밀번호 형식이 유효한지 체크하는 엔드포인트
     * 
     * @param request 비밀번호 체크 요청 객체
     * @return 비밀번호 유효성 체크 결과 응답
     */
    @PostMapping("check/password")
    public ResponseEntity<ApiDataResponse> checkPassword(
            @RequestBody @Valid CheckPasswordRequest request) {
        log.info("비밀번호 형식 체크 요청");
        // 비밀번호 정규식을 사용하여 형식 유효성 검사 후 결과 반환
        boolean isValid = request.password.matches(PASSWORD_PATTERN);
        if (isValid) {
            log.info("비밀번호 형식이 유효함");
        } else {
            log.warn("비밀번호 형식이 유효하지 않음");
        }
        return ResponseEntity
                .ok(new ApiDataResponse("Password format check", isValid, true));
    }

    /**
     * 사용자 등록을 처리하는 엔드포인트
     * 
     * @param request 등록 요청 객체
     * @param session 현재 세션 객체
     * @return 등록 결과에 대한 응답
     */
    @PostMapping("register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterRequest request, HttpSession session) {
        log.info("사용자 등록 요청: [{}]", request.username);
        // 이메일 존재 여부 확인
        boolean isExistEmail = userService.existEmail(request.email);
        if (isExistEmail) {
            log.warn("이메일 이미 사용 중: [{}]", request.email);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(ERROR_EMAIL_ALREADY_USED, ApiResponse.Code.USED));
        }
        // 사용자 이름 존재 여부 확인
        boolean isExistUserName = userService.existUsername(request.username);
        if (isExistUserName) {
            log.warn("사용자 이름 이미 사용 중: [{}]", request.username);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(ERROR_USERNAME_ALREADY_USED, ApiResponse.Code.USED));
        }

        // 세션에 저장된 사용자 이름 확인
        String checkedUsername = (String) session.getAttribute(SESSION_CHECKED_USER);
        if (checkedUsername == null) {
            log.warn("사용자 이름 체크가 수행되지 않음");
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_USERNAME_CHECK_NOT_PERFORMED, false));
        }

        // 세션에 저장된 이메일 확인
        String checkedEmail = (String) session.getAttribute(SESSION_CHECKED_EMAIL);
        if (checkedEmail == null) {
            log.warn("이메일 체크가 수행되지 않음");
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_EMAIL_CHECK_NOT_PERFORMED, false));
        }

        // 요청된 사용자 이름과 세션에 저장된 사용자 이름이 일치하지 않는 경우
        if ((!checkedUsername.equals(request.username))) {
            log.warn("세션에 저장된 사용자 이름과 일치하지 않음: [{}]", request.username);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_USERNAME_MISMATCH, false));
        }

        // 요청된 이메일과 세션에 저장된 이메일이 일치하지 않는 경우
        if (!checkedEmail.equals(request.email)) {
            log.warn("세션에 저장된 이메일과 일치하지 않음: [{}]", request.email);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_EMAIL_MISMATCH, false));
        }

        try {
            // 사용자 정보를 저장하고 해당 사용자의 ID를 반환
            Long id = userService.save(request.username, request.email, request.password);
            log.info("사용자 등록 성공: [{}]", request.username);

            // 사용자 등록 성공 시 생성된 리소스 위치와 성공 메시지 반환
            URI location = URI.create(String.format("/api/users/%d/info", id));
            return ResponseEntity.created(location)
                    .body(new ApiResponse(MESSAGE_REGISTER_SUCCESSFUL, true));
        } catch (Exception e) {
            // 예외 발생 시 에러 로그와 BAD_REQUEST 상태 반환
            log.error("사용자 등록 중 오류 발생: [{}]", e.toString());
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }

    /**
     * 로그인 상태를 체크하는 엔드포인트
     * 
     * @return 로그인 상태 정보 응답
     */
    @GetMapping("check/status")
    public ResponseEntity<ApiResponse> checkLoginStatus() {
        log.info("로그인 상태 체크 요청");
        // 현재 로그인 상태 확인 후 결과 반환
        return ResponseEntity.ok(new ApiDataResponse(MESSAGE_LOGIN_STATUS, AuthUtils.checkLogin(), true));
    }

    /**
     * 현재 로그인된 사용자 정보를 반환하는 엔드포인트
     * 
     * @param request HTTP 요청 객체
     * @return 로그인된 사용자 정보 응답
     */
    @GetMapping("me")
    public ResponseEntity<ApiResponse> me(HttpServletRequest request) {
        log.info("로그인된 사용자 정보 요청");
        // 현재 로그인된 사용자 정보 가져옴
        UserDTO userDTO = AuthUtils.getCurrentUser();
        return ResponseEntity.ok(new ApiDataResponse(MESSAGE_LOGIN_STATUS, userDTO, true));
    }

    /**
     * 비밀번호 변경을 처리하는 엔드포인트
     * 
     * @param request 비밀번호 변경 요청 객체
     * @return 비밀번호 변경 결과 응답
     */
    @PutMapping("password")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        log.info("비밀번호 변경 요청");
        // 현재 로그인된 사용자 정보 가져옴
        UserDTO user = AuthUtils.getCurrentUser();

        try {
            // 사용자 비밀번호 변경 처리
            userService.changePassword(user.getUsername(), request.password);
            log.info("비밀번호 변경 성공: [{}]", user.getUsername());
            return ResponseEntity.ok(new ApiResponse(MESSAGE_PASSWORD_CHANGE_SUCCESSFUL, true));
        } catch (RuntimeException e) {
            // 예외 발생 시 에러 로그 기록 후 BAD_REQUEST 응답 반환
            log.error(ERROR, e.toString());
            return ResponseEntity.badRequest().body(new ApiExceptionResponse(e));
        }
    }

    /**
     * 사용자 이름 체크 요청을 위한 레코드 정의
     */
    public record CheckUsernameRequest(
            @NotNull(message = "Missing required parameter") String username) {
    }

    /**
     * 비밀번호 체크 요청을 위한 레코드 정의
     */
    public record CheckPasswordRequest(
            @NotNull(message = "Missing required parameter") String password) {
    }

    /**
     * 비밀번호 변경 요청을 위한 레코드 정의
     */
    public record ChangePasswordRequest(
            @NotNull(message = "Missing required parameter") @Pattern(regexp = PASSWORD_PATTERN, message = "Invalid password format") String password) {
    }

    /**
     * 이메일 체크 요청을 위한 레코드 정의
     */
    public record CheckEmailRequest(
            @NotNull(message = "Missing required parameter") String email) {
    }

    /**
     * 사용자 등록 요청을 위한 레코드 정의
     */
    public record RegisterRequest(
            @NotNull(message = "Missing required parameter") String username,
            @NotNull(message = "Missing required parameter") String email,
            @NotNull(message = "Missing required parameter") String password) {
    }
}
