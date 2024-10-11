package shop.samgak.mini_board.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.user.entities.User;
import shop.samgak.mini_board.utility.ApiResponse;
import shop.samgak.mini_board.utility.UserSessionHelper;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSessionAuthentication
        implements AuthenticationSuccessHandler, LogoutSuccessHandler, AuthenticationEntryPoint,
        AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;
    private Optional<User> lastFoundUser;
    private final UserSessionHelper userSessionHelper;

    public static final String AUTHORITY_USER = "USER";
    public static final String ERROR_MSG_CANNOT_FIND_USER = "Cannot Found User : %s";
    public static final String SUCCESS_MSG_LOGIN = "Login successful";
    public static final String SUCCESS_MSG_LOGOUT = "Logout successful";
    public static final String ERROR_MSG_INVALID_CREDENTIALS = "Invalid username or password";
    public static final String ERROR_MSG_AUTH_FAILED = "Authentication failed";
    public static final String ERROR_MSG_AUTH_REQUIRED = "Authentication required";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        User user = lastFoundUser.orElseThrow();
        userSessionHelper.setUserSession(user, request.getSession());

        ApiResponse apiResponse = new ApiResponse(SUCCESS_MSG_LOGIN, true);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        lastFoundUser = null;
        ApiResponse apiResponse;
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        if (exception instanceof BadCredentialsException) {
            apiResponse = new ApiResponse(ERROR_MSG_INVALID_CREDENTIALS, false);
        } else {
            apiResponse = new ApiResponse(ERROR_MSG_AUTH_FAILED, false);
        }
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        ApiResponse apiResponse = new ApiResponse(SUCCESS_MSG_LOGOUT, true);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {
        ApiResponse apiResponse = new ApiResponse(ERROR_MSG_AUTH_REQUIRED, false);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
