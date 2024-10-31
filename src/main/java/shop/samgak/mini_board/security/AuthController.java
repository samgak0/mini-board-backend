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

        log.info("사용자 '{}'의 로그인 요청 처리 시작", loginRequest.username);
        // 사용자의 인증 정보 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.username, loginRequest.password);
        // 인증 처리
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        // 인증된 사용자 정보 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // SecurityContext를 세션에 저장
        AuthUtils.saveSessionSecurityContext(SecurityContextHolder.getContext(), session);
        log.info("사용자 '{}'의 로그인 성공", loginRequest.username);
        return ResponseEntity.ok().body(new ApiResponse("Login successful", true));
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
        log.info("사용자의 로그아웃(GET) 요청 처리 시작");
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
        log.info("사용자의 로그아웃(POST) 요청 처리 시작");
        if (session != null) {
            if (session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY) != null) {
                // 세션 무효화
                session.invalidate();
                log.info("세션 무효화 완료");
            } else {
                log.warn("로그인되지 않은 사용자의 로그아웃 시도");
                throw new UserNotLoginException();
            }
        } else {
            log.warn("세션이 없는 사용자의 로그아웃 시도");
            throw new UserNotLoginException();
        }
        // SecurityContext 초기화
        SecurityContextHolder.clearContext();
        log.info("SecurityContext 초기화 완료");

        return ResponseEntity.ok().body(new ApiResponse("Logout successful", true));
    }

    /**
     * 로그인 요청을 위한 레코드 정의
     */
    public record LoginRequest(
            @NotNull(message = "Missing required parameter") String username,
            @NotNull(message = "Missing required parameter") String password) {
    }
}
