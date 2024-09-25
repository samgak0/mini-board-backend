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

    public static final String SESSION_CHECKED_USER = "checked_user_name";
    public static final String SESSION_CHECKED_EMAIL = "checked_email";

    public static final String ERROR = "Error occurred";
    public static final String ERROR_USERNAME_REQUIRED = "Username is required";
    public static final String ERROR_EMAIL_REQUIRED = "Email is required";
    public static final String ERROR_PASSWORD_REQUIRED = "Password is required";

    public static final String ERROR_INVALID_EMAIL_FORMAT = "Invalid email format";
    public static final String ERROR_USERNAME_CHECK_NOT_PERFORMED = "Username availability check not performed";
    public static final String ERROR_EMAIL_CHECK_NOT_PERFORMED = "Email availability check not performed";
    public static final String ERROR_USERNAME_ALREADY_USED = "Username is already in use";
    public static final String ERROR_EMAIL_ALREADY_USED = "Email is already in use";
    public static final String ERROR_USERNAME_MISMATCH = "Username does not match the checked username";
    public static final String ERROR_EMAIL_MISMATCH = "Email does not match the checked email";
    public static final String ERROR_AUTHENTICATION_REQUIRED = "Authentication is required";

    public static final String MESSAGE_PASSWORD_CHANGE_SUCCESSFUL = "Password change successful";
    public static final String MESSAGE_REGISTER_SUCCESSFUL = "Registration successful";
    public static final String MESSAGE_EMAIL_AVAILABLE = "Email is available";
    public static final String MESSAGE_USERNAME_AVAILABLE = "Username is available";
    public static final String MESSAGE_LOGIN_STATUS = "Login status";

    final UserService userService;

    @PostMapping("check/username")
    public ResponseEntity<ApiResponse> checkUsername(@RequestParam String username, HttpSession session) {
        try {
            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(ERROR_USERNAME_REQUIRED, false));
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
    public ResponseEntity<ApiResponse> checkEmail(@RequestParam(required = false) String email, HttpSession session) {
        try {
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse(ERROR_EMAIL_REQUIRED, false));
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
    public ResponseEntity<ApiResponse> register(@RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password, HttpSession session) {

        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_USERNAME_REQUIRED, false));
        }

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_EMAIL_REQUIRED, false));
        }

        if (password == null || password.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_PASSWORD_REQUIRED, false));
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

        if ((!checkedUsername.equals(username))) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_USERNAME_MISMATCH, false));
        }

        if (!checkedEmail.equals(email)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_EMAIL_MISMATCH, false));
        }

        try {
            Long id = userService.save(username, email, password);
            URI location = URI.create(String.format("/api/users/%d/info", id));
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
                    .body(new ApiResponse(ERROR_AUTHENTICATION_REQUIRED, false));
        }
    }
}
