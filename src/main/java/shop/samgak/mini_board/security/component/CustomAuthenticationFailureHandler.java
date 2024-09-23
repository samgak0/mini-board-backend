package shop.samgak.mini_board.security.component;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
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
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        ApiResponse apiResponse;
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        if (exception instanceof BadCredentialsException) {
            apiResponse = new ApiResponse("Invalid username or password", false);
        } else {
            apiResponse = new ApiResponse("Authentication failed", false);
        }
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
