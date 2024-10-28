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

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users/")
public class UserController {
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$";

    public static final String SESSION_CHECKED_USER = "checked_user_name";
    public static final String SESSION_CHECKED_EMAIL = "checked_email";

    public static final String ERROR = "Error occurred";

    public static final String ERROR_INVALID_EMAIL_FORMAT = "Invalid email format";
    public static final String ERROR_USERNAME_CHECK_NOT_PERFORMED = "Username availability check not performed";
    public static final String ERROR_EMAIL_CHECK_NOT_PERFORMED = "Email availability check not performed";
    public static final String ERROR_USERNAME_ALREADY_USED = "Username is already in use";
    public static final String ERROR_EMAIL_ALREADY_USED = "Email is already in use";
    public static final String ERROR_USERNAME_MISMATCH = "Username does not match the checked username";
    public static final String ERROR_EMAIL_MISMATCH = "Email does not match the checked email";

    public static final String MESSAGE_PASSWORD_CHANGE_SUCCESSFUL = "Password change successful";
    public static final String MESSAGE_REGISTER_SUCCESSFUL = "Registration successful";
    public static final String MESSAGE_EMAIL_AVAILABLE = "Email is available";
    public static final String MESSAGE_USERNAME_AVAILABLE = "Username is available";
    public static final String MESSAGE_LOGIN_STATUS = "Login status";

    final UserService userService;

    @PostMapping("check/username")
    public ResponseEntity<ApiResponse> checkUsername(@RequestBody @Valid CheckUsernameRequest request,
            HttpSession session) {
        boolean isExistUserName = userService.existUsername(request.username);
        if (!isExistUserName) {
            session.setAttribute(SESSION_CHECKED_USER, request.username);
            return ResponseEntity.ok().body(new ApiResponse(MESSAGE_USERNAME_AVAILABLE, true));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(ERROR_USERNAME_ALREADY_USED, ApiResponse.Code.USED));
        }
    }

    @PostMapping("check/email")
    public ResponseEntity<ApiResponse> checkEmail(@RequestBody @Valid CheckEmailRequest request,
            HttpSession session) {
        EmailValidator validator = EmailValidator.getInstance();
        if (!validator.isValid(request.email)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_INVALID_EMAIL_FORMAT, false));
        }

        boolean isExistEmail = userService.existEmail(request.email);
        if (!isExistEmail) {
            session.setAttribute(SESSION_CHECKED_EMAIL, request.email);
            return ResponseEntity.ok().body(new ApiResponse(MESSAGE_EMAIL_AVAILABLE, true));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(ERROR_EMAIL_ALREADY_USED, ApiResponse.Code.USED));
        }
    }

    @PostMapping("check/password")
    public ResponseEntity<ApiDataResponse> checkPassword(
            @RequestBody @Valid CheckPasswordRequest request) {
        return ResponseEntity
                .ok(new ApiDataResponse("Password format check", request.password.matches(PASSWORD_PATTERN), true));
    }

    @PostMapping("register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterRequest request, HttpSession session) {
        boolean isExistEmail = userService.existEmail(request.email);
        if (isExistEmail) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(ERROR_EMAIL_ALREADY_USED, ApiResponse.Code.USED));
        }
        boolean isExistUserName = userService.existUsername(request.username);
        if (isExistUserName) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(ERROR_USERNAME_ALREADY_USED, ApiResponse.Code.USED));
        }

        String checkedUsername = (String) session.getAttribute(SESSION_CHECKED_USER);
        if (checkedUsername == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_USERNAME_CHECK_NOT_PERFORMED, false));
        }

        String checkedEmail = (String) session.getAttribute(SESSION_CHECKED_EMAIL);
        if (checkedEmail == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_EMAIL_CHECK_NOT_PERFORMED, false));
        }

        if ((!checkedUsername.equals(request.username))) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_USERNAME_MISMATCH, false));
        }

        if (!checkedEmail.equals(request.email)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_EMAIL_MISMATCH, false));
        }

        try {
            Long id = userService.save(request.username, request.email, request.password);

            URI location = URI.create(String.format("/api/users/%d/info", id));
            return ResponseEntity.created(location)
                    .body(new ApiResponse(MESSAGE_REGISTER_SUCCESSFUL, true));
        } catch (Exception e) {
            log.error(ERROR, e.toString());
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }

    @GetMapping("check/status")
    public ResponseEntity<ApiResponse> checkLoginStatus() {
        return ResponseEntity.ok(new ApiDataResponse(MESSAGE_LOGIN_STATUS, AuthUtils.checkLogin(), true));
    }

    @GetMapping("me")
    public ResponseEntity<ApiResponse> me(HttpServletRequest request) {
        UserDTO userDTO = AuthUtils.getCurrentUser();
        return ResponseEntity.ok(new ApiDataResponse(MESSAGE_LOGIN_STATUS, userDTO, true));
    }

    @PutMapping("password")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {

        UserDTO user = AuthUtils.getCurrentUser();

        try {
            userService.changePassword(user.getUsername(), request.password);
            return ResponseEntity.ok(new ApiResponse(MESSAGE_PASSWORD_CHANGE_SUCCESSFUL, true));
        } catch (RuntimeException e) {
            log.error(ERROR, e.toString());
            return ResponseEntity.badRequest().body(new ApiExceptionResponse(e));
        }
    }

    public record CheckUsernameRequest(
            @NotNull(message = "Missing required parameter") String username) {
    }

    public record CheckPasswordRequest(
            @NotNull(message = "Missing required parameter") String password) {
    }

    public record ChangePasswordRequest(
            @NotNull(message = "Missing required parameter") @Pattern(regexp = PASSWORD_PATTERN, message = "Invalid password format") String password) {
    }

    public record CheckEmailRequest(
            @NotNull(message = "Missing required parameter") String email) {
    }

    public record RegisterRequest(
            @NotNull(message = "Missing required parameter") String username,
            @NotNull(message = "Missing required parameter") String email,
            @NotNull(message = "Missing required parameter") String password) {
    }

}
