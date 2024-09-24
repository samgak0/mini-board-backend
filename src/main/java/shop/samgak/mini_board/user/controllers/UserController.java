package shop.samgak.mini_board.user.controllers;

import java.net.URI;
import java.util.Optional;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.user.services.UserService;
import shop.samgak.mini_board.utility.ApiDataResponse;
import shop.samgak.mini_board.utility.ApiResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users/")
public class UserController {

    private static final String SESSION_CHECKED_USER = "checked_user_name";
    private static final String SESSION_CHECKED_EMAIL = "checked_email";

    private static final String ERROR = "Error occurred";

    private static final String ERROR_USERNAME_EMPTY = "Username is empty";
    private static final String ERROR_EMAIL_EMPTY = "Email is empty";
    private static final String ERROR_INVALID_EMAIL_FORMAT = "Invalid email format";
    private static final String ERROR_USERNAME_CHECK_NOT_PERFORMED = "Username check not performed";
    private static final String ERROR_EMAIL_CHECK_NOT_PERFORMED = "Email check not performed";
    private static final String ERROR_USERNAME_ALREADY_USED = "Username already used";
    private static final String ERROR_EMAIL_ALREADY_USED = "Email already used";

    private static final String MESSAGE_PASSWORD_CHANGE_SUCCESSFUL = "Password Change successful";
    private static final String MESSAGE_REGISTER_SUCCESSFUL = "Register successful";
    private static final String MESSAGE_EMAIL_AVAILABLE = "Email is available";
    private static final String MESSAGE_USERNAME_AVAILABLE = "Username is available";
    private static final String MESSAGE_LOGIN_STATUS = "Login status";
    private static final String MESSAGE_AUTHENTICATION_REQUIRED = "Authentication required";

    final UserService userService;

    @PostMapping("check/username")
    public ResponseEntity<ApiResponse> checkUsername(@RequestParam String username, HttpSession session) {
        try {
            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(ERROR_USERNAME_EMPTY, false));
            }

            boolean isExistUserName = userService.existUsername(username);
            if (!isExistUserName) {
                session.setAttribute(SESSION_CHECKED_USER, username);
                return ResponseEntity.ok().body(new ApiResponse(MESSAGE_USERNAME_AVAILABLE, true));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse(ERROR_USERNAME_ALREADY_USED, ApiResponse.Code.USED));
            }
        } catch (Exception e) {
            log.error(ERROR, e.toString());
            return ResponseEntity.internalServerError().body(new ApiResponse(e.getMessage(), false));
        }
    }

    @PostMapping("check/email")
    public ResponseEntity<ApiResponse> checkEmail(@RequestParam String email, HttpSession session) {
        try {
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse(ERROR_EMAIL_EMPTY, false));
            }

            EmailValidator validator = EmailValidator.getInstance();
            if (!validator.isValid(email)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(ERROR_INVALID_EMAIL_FORMAT, false));
            }

            boolean isExistEmail = userService.existEmail(email);
            if (!isExistEmail) {
                session.setAttribute(SESSION_CHECKED_EMAIL, email);
                return ResponseEntity.ok().body(new ApiResponse(MESSAGE_EMAIL_AVAILABLE, true));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse(ERROR_EMAIL_ALREADY_USED, ApiResponse.Code.USED));
            }
        } catch (Exception e) {
            log.error(ERROR, e.toString());
            return ResponseEntity.internalServerError().body(new ApiResponse(e.getMessage(), false));
        }
    }

    @PostMapping("register")
    public ResponseEntity<ApiResponse> register(@RequestParam String username, @RequestParam String email,
            @RequestParam String password, HttpSession session) {
        Object checkedUsername = session.getAttribute(SESSION_CHECKED_USER);
        if (checkedUsername == null || !(checkedUsername instanceof String)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_USERNAME_CHECK_NOT_PERFORMED, false));
        }
        Object checkedEmail = session.getAttribute(SESSION_CHECKED_EMAIL);
        if (checkedEmail == null || !(checkedEmail instanceof String)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_EMAIL_CHECK_NOT_PERFORMED, false));
        }

        try {
            Long id = userService.save(username, email, password);
            URI location = URI.create("/api/users/" + id + "/info");
            return ResponseEntity.created(location)
                    .body(new ApiResponse(MESSAGE_REGISTER_SUCCESSFUL, true));
        } catch (Exception e) {
            log.error(ERROR, e.toString());
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }

    @GetMapping("status")
    public ResponseEntity<ApiResponse> checkLoginStatus() {
        return ResponseEntity.ok(new ApiDataResponse(MESSAGE_LOGIN_STATUS, userService.isLogin(), true));
    }

    @PutMapping("password")
    public ResponseEntity<ApiResponse> changePassword(@RequestParam String password) {

        Optional<UserDetails> user = userService.getCurrentUser();

        if (user.isPresent()) {
            try {
                userService.changePassword(user.get().getUsername(), password);
                return ResponseEntity.ok(new ApiResponse(MESSAGE_PASSWORD_CHANGE_SUCCESSFUL, true));
            } catch (Exception e) {
                log.error(ERROR, e.toString());
                return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(MESSAGE_AUTHENTICATION_REQUIRED, false));
        }
    }
}
