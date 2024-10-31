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
import shop.samgak.mini_board.utility.ApiUnauthrizationResponse;

/**
 * 인증되지 않은 사용자가 보호된 자원에 접근할 때 처리하는 엔트리 포인트 클래스
 * <p>
 * 이 클래스는 인증이 필요한 요청에 대해 인증되지 않은 상태에서 접근하려고 할 때,
 * SC_UNAUTHORIZED(401) 상태 코드를 응답으로 보내고, JSON 형태의 에러 메시지를 반환합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * 인증되지 않은 요청이 발생했을 때 실행되는 메서드
     * <p>
     * 이 메서드는 인증되지 않은 사용자가 보호된 자원에 접근할 경우 실행되며,
     * 응답으로 UNAUTHORIZED(401) 상태 코드와 함께 에러 메시지를 JSON 형식으로 반환합니다.
     * 
     * @param request       HTTP 요청 객체
     * @param response      HTTP 응답 객체
     * @param authException 인증 예외 객체
     * @throws IOException 입출력 예외가 발생할 경우
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        // 인증 실패에 대한 로그를 기록
        log.warn("인증되지 않은 접근 시도: URI={}, Exception={}", request.getRequestURI(), authException.getMessage());

        // ApiUnauthrizationResponse 객체를 생성하여 응답으로 사용
        ApiUnauthrizationResponse apiResponse = new ApiUnauthrizationResponse();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
