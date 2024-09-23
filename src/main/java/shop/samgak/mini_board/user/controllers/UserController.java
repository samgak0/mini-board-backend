package shop.samgak.mini_board.user.controllers;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.user.services.UserService;
import shop.samgak.mini_board.utility.ApiResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users/")
public class UserController {

    private static final String SESSION_CHECKED_USER = "checked_user_name";
    private static final String SESSION_CHECKED_EMAIL = "checked_email";

    final UserService userService;

    @PostMapping("check/username")
    public ResponseEntity<ApiResponse> checkUsername(@RequestParam String username, HttpSession session) {
        try {
            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse("username is empty", ApiResponse.Code.FAILURE));
            }

            boolean isExistUserName = userService.existUsername(username);
            if (!isExistUserName) {
                session.setAttribute(SESSION_CHECKED_USER, username);
                return ResponseEntity.ok().body(new ApiResponse("username is available", true));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse("username already used", ApiResponse.Code.USED));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ApiResponse(e.getMessage(), false));
        }
    }

    @PostMapping("check/email")
    public ResponseEntity<ApiResponse> checkEmail(@RequestParam String email, HttpSession session) {
        try {
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse("email is empty", ApiResponse.Code.FAILURE));
            }

            boolean isExistUserName = userService.existEmail(email);
            if (!isExistUserName) {
                session.setAttribute(SESSION_CHECKED_EMAIL, email);
                return ResponseEntity.ok().body(new ApiResponse("email is available", true));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse("username already used", ApiResponse.Code.USED));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ApiResponse(e.getMessage(), false));
        }
    }

    @PostMapping("register")
    public ResponseEntity<ApiResponse> register(@RequestParam String username, @RequestParam String email,
            @RequestParam String password, HttpSession session) {
        Object checkedUsername = session.getAttribute(SESSION_CHECKED_USER);
        if (checkedUsername == null || !(checkedUsername instanceof String)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("username check not performed", false));
        }
        Object checkedEmail = session.getAttribute(SESSION_CHECKED_EMAIL);
        if (checkedEmail == null || !(checkedEmail instanceof String)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("email check not performed", false));
        }

        try {
            Long id = userService.save(username, email, password);
            URI location = URI.create("/api/users/" + id + "/info");
            return ResponseEntity.created(location)
                    .body(new ApiResponse("Register successful", true));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), false));
        }
    }

    @PutMapping("{id}/password")
    public ResponseEntity<ApiResponse> changePassword(@PathVariable String id) {
        return ResponseEntity
                .ok(new ApiResponse("change successful " + id, true));
    }
}