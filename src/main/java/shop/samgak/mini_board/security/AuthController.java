package shop.samgak.mini_board.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.exceptions.UserNotLoginException;
import shop.samgak.mini_board.utility.ApiResponse;
import shop.samgak.mini_board.utility.ApiSuccessResponse;
import shop.samgak.mini_board.utility.AuthUtils;

/**
 * 인증과 관련된 API 요청을 처리하는 컨트롤러 정의
 */
@Slf4j
@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    /**
     * 사용자의 로그인 요청을 처리하는 엔드포인트
     * 
     * @param loginRequest 사용자 로그인 요청 객체
     * @param session      현재 세션 객체
     * @return 로그인 성공 여부에 대한 응답
     */
    @PostMapping("login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest,
            HttpSession session) {
        log.info("Request to login for user [{}]", loginRequest.username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.username, loginRequest.password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AuthUtils.saveSessionSecurityContext(SecurityContextHolder.getContext(), session);
        return ResponseEntity.ok().body(new ApiSuccessResponse("Login successful"));
    }

    /**
     * 사용자의 로그아웃 요청을 처리하는 엔드포인트 (GET 요청)
     * 
     * @param request HTTP 요청 객체
     * @param session 현재 세션 객체
     * @return 로그아웃 성공 여부에 대한 응답
     */
    @GetMapping("logout")
    public ResponseEntity<?> logoutGet(HttpServletRequest request, HttpSession session) {
        return logoutPost(request, session);
    }

    /**
     * 사용자의 로그아웃 요청을 처리하는 엔드포인트 (POST 요청)
     * 
     * @param request HTTP 요청 객체
     * @param session 현재 세션 객체
     * @return 로그아웃 성공 여부에 대한 응답
     */
    @PostMapping("logout")
    public ResponseEntity<?> logoutPost(HttpServletRequest request, HttpSession session) {
        log.info("User logging out");
        if (session != null) {
            if (session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY) != null) {
                // 세션 무효화
                session.invalidate();
            } else {
                throw new UserNotLoginException();
            }
        } else {
            throw new UserNotLoginException();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().body(new ApiSuccessResponse("Logout successful"));
    }

    /**
     * 로그인 요청을 위한 레코드 정의
     */
    public record LoginRequest(
            @NotNull(message = "Missing required parameter") String username,
            @NotNull(message = "Missing required parameter") String password) {
    }
}
