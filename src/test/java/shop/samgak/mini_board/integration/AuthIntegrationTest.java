package shop.samgak.mini_board.integration;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 통합 테스트 클래스 AuthIntegrationTest.
 * Spring Boot의 TestRestTemplate을 사용해 인증 관련 엔드포인트를 테스트합니다.
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AuthIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        HttpEntity<String> requestEntity = createRequestEntity(loginRequest);

        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, requestEntity, String.class);
        assertSuccessResponse(response, HttpStatus.OK, "Login successful");
    }

    /**
     * 성공적인 응답을 검증하는 메서드.
     * 
     * @param response        응답 객체
     * @param status          기대하는 상태 코드
     * @param expectedMessage 기대하는 메시지
     */
    private void assertSuccessResponse(ResponseEntity<String> response, HttpStatus status, String expectedMessage) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains(expectedMessage);
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
        HttpEntity<String> requestEntity = createRequestEntity(loginRequest);
        // ResponseEntity<String> response = restTemplate.postForEntity(loginUrl,
        // requestEntity, String.class);
        try {
            restTemplate.postForEntity(loginUrl, requestEntity, String.class);
            fail("Expected ResourceAccessException to be thrown");
        } catch (ResourceAccessException e) {
            assertThat(e).isInstanceOf(ResourceAccessException.class);
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
        HttpEntity<String> requestEntity = createRequestEntity(loginRequest);

        // 요청을 전송하고 BAD_REQUEST 응답인지 확인
        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, requestEntity, String.class);
        assertErrorResponse(response, HttpStatus.BAD_REQUEST,
                "password: Missing required parameter; username: Missing required parameter;");
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
        HttpEntity<String> requestEntity = createRequestEntity(loginRequest);
        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, requestEntity, String.class);
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, "username: Missing required parameter;");
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
        HttpEntity<String> requestEntity = createRequestEntity(loginRequest);

        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, requestEntity, String.class);
        assertErrorResponse(response, HttpStatus.BAD_REQUEST, "password: Missing required parameter;");
    }

    /**
     * 요청 본문을 JSON 형식으로 변환하고 HTTP 엔티티를 생성
     * 
     * @param body 요청 본문 데이터
     * @return 생성된 HTTP 엔티티
     * @throws Exception JSON 변환 중 발생할 수 있는 예외
     */
    private HttpEntity<String> createRequestEntity(Map<String, String> body) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = objectMapper.writeValueAsString(body);
        return new HttpEntity<>(requestBody, headers);
    }

    /**
     * 오류 응답을 검증
     * 
     * @param response        응답 객체
     * @param status          기대하는 상태 코드
     * @param expectedMessage 기대하는 메시지
     * @throws Exception JSON 파싱 중 발생할 수 있는 예외
     */
    private void assertErrorResponse(ResponseEntity<String> response, HttpStatus status, String expectedMessage)
            throws Exception {

        Map<String, String> responseBody = objectMapper.readValue(response.getBody(),
                new TypeReference<Map<String, String>>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(responseBody.get("message")).isEqualTo(expectedMessage);
        assertThat(responseBody.get("code")).isEqualTo("FAILURE");
    }
}
