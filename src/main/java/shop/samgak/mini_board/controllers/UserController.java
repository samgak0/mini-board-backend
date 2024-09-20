package shop.samgak.mini_board.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.dto.ApiResponse;
import shop.samgak.mini_board.services.UserService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users/")
public class UserController {

    final UserService userService;

    @PostMapping("register")
    public ResponseEntity<ApiResponse> register() {
        return null;
        // return ResponseEntity.ok(new ApiResponse("success", HttpStatus.ACCEPTED));
    }

    @PostMapping("login")
    public ResponseEntity<String> login() {
        // userService.login();
        return ResponseEntity.ok("success");
    }

    @PostMapping("logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("success");
    }

    @PutMapping
    public ResponseEntity<String> changePassword() {
        // userService.changePassword();
        return ResponseEntity.ok("success");
    }
}
/*
 * 
 * #### A. **사용자 API**
 * - **POST /api/users/register**: 회원가입
 * - **POST /api/users/login**: 로그인
 * - **POST /api/users/logout**: 로그아웃
 * - **PUT /api/users/{id}/password**: 비밀번호 변경
 */