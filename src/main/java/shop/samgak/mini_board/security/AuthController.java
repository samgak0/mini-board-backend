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

@Slf4j
@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @PostMapping("login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest,
            HttpSession session) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.username, loginRequest.password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AuthUtils.saveSessionSecurityContext(SecurityContextHolder.getContext(), session);
        return ResponseEntity.ok().body(new ApiResponse("Login successful", true));
    }

    @GetMapping("logout")
    public ResponseEntity<?> logoutGet(HttpServletRequest request, HttpSession session) {
        return logoutPost(request, session);
    }

    @PostMapping("logout")
    public ResponseEntity<?> logoutPost(HttpServletRequest request, HttpSession session) {
        if (session != null) {
            if (session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY) != null) {
                session.invalidate();
            } else {
                throw new UserNotLoginException();
            }
        } else {
            throw new UserNotLoginException();
        }
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().body(new ApiResponse("Logout successful", true));
    }

    public record LoginRequest(
            @NotNull(message = "Missing required parameter: username") String username,
            @NotNull(message = "Missing required parameter: password") String password) {
    }
}