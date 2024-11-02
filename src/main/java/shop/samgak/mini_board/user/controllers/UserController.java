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

        log.info("Checking username availability: [{}]", request.username);
        boolean isExistUserName = userService.existUsername(request.username);
        if (!isExistUserName) {
            session.setAttribute(SESSION_CHECKED_USER, request.username);
            return ResponseEntity.ok().body(new ApiResponse("Username is available", true));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse("Username is already in use", ApiResponse.Code.USED));
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
        log.info("Checking email availability: [{}]", request.email);
        EmailValidator validator = EmailValidator.getInstance();
        if (!validator.isValid(request.email)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Invalid email format", false));
        }

        boolean isExistEmail = userService.existEmail(request.email);
        if (!isExistEmail) {
            session.setAttribute(SESSION_CHECKED_EMAIL, request.email);
            return ResponseEntity.ok().body(new ApiResponse("Email is available", true));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse("Email is already in use", ApiResponse.Code.USED));
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
        log.info("Checking password format for: [{}]", request.password);
        boolean isValid = request.password.matches(PASSWORD_PATTERN);
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
        log.info("trying to register user with username: [{}] and email: [{}]", request.username, request.email);
        boolean isExistEmail = userService.existEmail(request.email);
        if (isExistEmail) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse("Email is already in use", ApiResponse.Code.USED));
        }

        boolean isExistUserName = userService.existUsername(request.username);
        if (isExistUserName) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse("Username is already in use", ApiResponse.Code.USED));
        }

        String checkedUsername = (String) session.getAttribute(SESSION_CHECKED_USER);
        if (checkedUsername == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Username check was not performed", false));
        }

        String checkedEmail = (String) session.getAttribute(SESSION_CHECKED_EMAIL);
        if (checkedEmail == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Email check was not performed", false));
        }

        if ((!checkedUsername.equals(request.username))) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Username does not match the confirmed username", false));
        }

        if (!checkedEmail.equals(request.email)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Email does not match the confirmed email", false));
        }

        Long id = userService.save(request.username, request.email, request.password);

        URI location = URI.create(String.format("/api/users/%d/info", id));
        return ResponseEntity.created(location)
                .body(new ApiResponse("Registration successful", true));

    }

    /**
     * 로그인 상태를 체크하는 엔드포인트
     * 
     * @return 로그인 상태 정보 응답
     */
    @GetMapping("check/status")
    public ResponseEntity<ApiResponse> checkLoginStatus() {
        log.info("Checking login status");
        return ResponseEntity.ok(new ApiDataResponse("Login status", AuthUtils.checkLogin(), true));
    }

    /**
     * 현재 로그인된 사용자 정보를 반환하는 엔드포인트
     * 
     * @param request HTTP 요청 객체
     * @return 로그인된 사용자 정보 응답
     */
    @GetMapping("me")
    public ResponseEntity<ApiResponse> me(HttpServletRequest request) {
        log.info("Fetching current logged-in user information");
        UserDTO userDTO = AuthUtils.getCurrentUser();
        return ResponseEntity.ok(new ApiDataResponse("Login status", userDTO, true));
    }

    /**
     * 비밀번호 변경을 처리하는 엔드포인트
     * 
     * @param request 비밀번호 변경 요청 객체
     * @return 비밀번호 변경 결과 응답
     */
    @PutMapping("password")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        log.info("Attempting to change password for current user");
        UserDTO user = AuthUtils.getCurrentUser();
        userService.changePassword(user.getUsername(), request.password);
        return ResponseEntity.ok(new ApiResponse("Password change successful", true));
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
