package shop.samgak.mini_board.integration;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 통합 테스트 클래스 AuthIntegrationTest.
 * Spring Boot의 RestClient를 사용해 인증 관련 엔드포인트를 테스트합니다.
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AuthIntegrationTest {

    private RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    private int port;

    @Value("${app.hostname:localhost}")
    String hostname;
    @Value("${app.secure:false}")
    boolean secure;

    @BeforeEach
    public void setup() {
        String baseUrl = (secure ? "https" : "http") + "://" + hostname + ":" + port;
        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    private final String loginUrl = "/api/auth/login";

    /**
     * 로그인 성공 테스트 - 유효한 사용자명과 비밀번호를 사용하여 로그인 요청을 보냅니다.
     * 
     * @throws Exception 요청 중 발생할 수 있는 예외
     */
    @Test
    public void testLoginSuccess() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "user");
        loginRequest.put("password", "password");

        // RestClient로 POST 요청 보내기
        ResponseEntity<String> response = restClient.post()
                .uri(loginUrl)
                .body(loginRequest) // JSON 변환은 RestClient가 자동 처리
                .retrieve()
                .toEntity(String.class);

        assertSuccessResponse(response, HttpStatus.OK, "Login successful");
    }

    /**
     * 로그인 실패 테스트 - 잘못된 비밀번호를 사용하여 로그인 시도합니다.
     * 
     * @throws Exception 요청 중 발생할 수 있는 예외
     */
    @Test
    public void testLoginFailure() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "user");
        loginRequest.put("password", "wrongpassword");

        try {
            restClient.post()
                    .uri(loginUrl)
                    .body(loginRequest)
                    .retrieve()
                    .toEntity(String.class);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 로그인 실패 테스트 - 사용자명과 비밀번호가 모두 없는 경우
     * 
     * @throws Exception 요청 중 발생할 수 있는 예외
     */
    @Test
    public void testLoginMissingBoth() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();

        try {
            restClient.post()
                    .uri(loginUrl)
                    .body(loginRequest)
                    .retrieve()
                    .toEntity(String.class);
        } catch (HttpClientErrorException e) {
            assertErrorResponse(e, HttpStatus.BAD_REQUEST,
                    "password: Missing required parameter; username: Missing required parameter;");
        }
    }

    /**
     * 로그인 실패 테스트 - 사용자명이 없는 경우
     * 
     * @throws Exception 요청 중 발생할 수 있는 예외
     */
    @Test
    public void testLoginMissingUsername() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("password", "password");

        try {
            restClient.post()
                    .uri(loginUrl)
                    .body(loginRequest)
                    .retrieve()
                    .toEntity(String.class);
        } catch (HttpClientErrorException e) {
            assertErrorResponse(e, HttpStatus.BAD_REQUEST, "username: Missing required parameter;");
        }
    }

    /**
     * 로그인 실패 테스트 - 비밀번호가 없는 경우
     * 
     * @throws Exception 요청 중 발생할 수 있는 예외
     */
    @Test
    public void testLoginMissingPassword() throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "user");

        try {
            restClient.post()
                    .uri(loginUrl)
                    .body(loginRequest)
                    .retrieve()
                    .toEntity(String.class);
        } catch (HttpClientErrorException e) {
            assertErrorResponse(e, HttpStatus.BAD_REQUEST, "password: Missing required parameter;");
        }
    }

    /**
     * 오류 응답을 검증
     * 
     * @param response        응답 객체
     * @param status          기대하는 상태 코드
     * @param expectedMessage 기대하는 메시지
     * @throws Exception JSON 파싱 중 발생할 수 있는 예외
     */
    private void assertErrorResponse(HttpClientErrorException e, HttpStatus status, String expectedMessage)
            throws Exception {

        Map<String, String> responseBody = objectMapper.readValue(e.getResponseBodyAs(String.class),
                new TypeReference<Map<String, String>>() {
                });

        assertThat(e.getStatusCode()).isEqualTo(status);
        assertThat(responseBody.get("message")).isEqualTo(expectedMessage);
        assertThat(responseBody.get("code")).isEqualTo("FAILURE");
    }

    /**
     * 성공적인 응답을 검증하는 메서드.
     * 
     * @param response        응답 객체
     * @param status          기대하는 상태 코드
     * @param expectedMessage 기대하는 메시지
     */
    private void assertSuccessResponse(ResponseEntity<String> response, HttpStatus status, String expectedMessage)
            throws JsonProcessingException {
        Map<String, String> responseBody = objectMapper.readValue(response.getBody(),
                new TypeReference<Map<String, String>>() {
                });
        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(response.getBody()).isNotNull();
        assertThat(responseBody.get("message")).isEqualTo(expectedMessage);
        assertThat(responseBody.get("code")).isEqualTo("SUCCESS");
    }
}
