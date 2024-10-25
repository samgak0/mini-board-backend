package shop.samgak.mini_board.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.utility.ApiResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public static final String AUTHORITY_USER = "USER";
    public static final String ERROR_MSG_CANNOT_FIND_USER = "Cannot Found User : %s";
    public static final String SUCCESS_MSG_LOGIN = "Login successful";
    public static final String SUCCESS_MSG_LOGOUT = "Logout successful";
    public static final String ERROR_MSG_INVALID_CREDENTIALS = "Invalid username or password";
    public static final String ERROR_MSG_AUTH_FAILED = "Authentication failed";
    public static final String ERROR_MSG_AUTH_REQUIRED = "Authentication required";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {
        ApiResponse apiResponse = new ApiResponse(ERROR_MSG_AUTH_REQUIRED, false);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
