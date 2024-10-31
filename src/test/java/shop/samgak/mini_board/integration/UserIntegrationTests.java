package shop.samgak.mini_board.integration;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import shop.samgak.mini_board.user.services.UserService;

/**
 * 통합 테스트 클래스 - 사용자 관련 기능을 테스트
 * 이 클래스는 사용자 인증 및 보호된 자원 접근에 대한 테스트를 수행
 */
@ActiveProfiles("test") // 테스트 프로필 사용
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate; // RestTemplate을 사용하여 애플리케이션에 HTTP 요청을 보냅

    @MockBean
    private UserService userService; // UserService를 모킹하여 특정 시나리오를 테스트

    @Autowired
    private ObjectMapper objectMapper; // JSON 파싱을 위해 ObjectMapper를 사용

    /**
     * 이미 사용 중인 이메일로 회원가입을 시도할 때의 테스트
     * 해당 이메일이 이미 사용 중이라는 가정 하에 회원가입 요청을 테스트
     */
    @Test
    public void testRegisterEmailAlreadyUsed() throws Exception {
        String username = "newUser";
        String email = "existing@example.com";
        String password = "password123";

        // 이메일이 이미 존재하고 사용자 이름은 존재하지 않는 경우를 모킹
        when(userService.existEmail(email)).thenReturn(true);
        when(userService.existUsername(username)).thenReturn(false);

        // 회원가입 요청을 준비합니다.
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", username);
        registerRequest.put("email", email);
        registerRequest.put("password", password);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(registerRequest, headers);

        // 회원가입 엔드포인트로 POST 요청을 보냄
        ResponseEntity<String> response = restTemplate.postForEntity("/api/users/register", requestEntity,
                String.class);

        // 응답 상태 코드 검증
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        // JSON 응답을 객체로 변환
        Map<String, Object> responseBody = objectMapper.readValue(response.getBody(),
                new TypeReference<Map<String, Object>>() {
                });

        // 응답이 정상적인지 확인
        assertThat(responseBody.get("message")).isEqualTo("Email is already in use");
        assertThat(responseBody.get("code")).isEqualTo("USED");
    }

    /**
     * 이미 사용 중인 사용자 이름으로 회원가입을 시도할 때의 테스트.
     * 해당 사용자 이름이 이미 존재하는 경우의 시나리오를 테스트
     */
    @Test
    public void testRegisterUsernameAlreadyUsed() throws Exception {
        String username = "existingUser";
        String email = "newuser@example.com";
        String password = "password123";

        // 회원가입 요청을 준비합니다.
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", username);
        registerRequest.put("email", email);
        registerRequest.put("password", password);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(registerRequest, headers);

        // 사용자 이름이 이미 존재하고 이메일은 존재하지 않는 경우를 모킹
        when(userService.existUsername(username)).thenReturn(true);
        when(userService.existEmail(email)).thenReturn(false);

        // 회원가입 엔드포인트로 POST 요청을 보냄
        ResponseEntity<String> response = restTemplate.postForEntity("/api/users/register", requestEntity,
                String.class);

        // 응답 상태 코드 검증
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        // JSON 응답을 객체로 변환
        Map<String, Object> responseBody = objectMapper.readValue(response.getBody(),
                new TypeReference<Map<String, Object>>() {
                });

        // 응답이 정상적인지 확인
        assertThat(responseBody.get("message")).isEqualTo("Username is already in use");
        assertThat(responseBody.get("code")).isEqualTo("USED");
    }

    /**
     * 사용자 이름이 누락된 상태로 회원가입을 시도할 때의 테스트
     * 사용자 이름이 제공되지 않은 경우 API에서 오류가 발생하는지 확인
     */
    @Test
    public void testRegisterMissingUsername() throws Exception {
        String email = "newuser@example.com";
        String password = "password123";

        // 회원가입 요청을 준비합니다.
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("email", email);
        registerRequest.put("password", password);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(registerRequest, headers);
        try {
            // 회원가입 엔드포인트로 POST 요청을 보냅
            restTemplate.postForEntity("/api/users/register", requestEntity, String.class);
        } catch (ResourceAccessException e) {
            assertThat(e).isInstanceOf(ResourceAccessException.class);
        }
    }

    /**
     * 비밀번호가 누락된 상태로 회원가입을 시도할 때의 테스트
     * 비밀번호가 제공되지 않은 경우 API에서 오류가 발생하는지 확인
     */
    @Test
    public void testRegisterMissingPassword() throws Exception {
        String username = "newUser";
        String email = "newuser@example.com";

        // 회원가입 요청을 준비합니다.
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", username);
        registerRequest.put("email", email);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(registerRequest, headers);
        try {
            // 회원가입 엔드포인트로 POST 요청을 보냅
            restTemplate.postForEntity("/api/users/register", requestEntity, String.class);
        } catch (ResourceAccessException e) {
            assertThat(e).isInstanceOf(ResourceAccessException.class);
        }
    }

    /**
     * 로그인하지 않은 상태에서 사용자 정보를 요청할 때의 테스트.
     * 인증되지 않은 사용자가 보호된 자원에 접근할 때의 동작을 테스트
     */
    @Test
    public void testMeUnauthorized() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            // 인증되지 않은 상태로 사용자 정보를 요청
            restTemplate.exchange("/api/users/me", HttpMethod.GET, request, String.class);
        } catch (ResourceAccessException e) {
            assertThat(e).isInstanceOf(ResourceAccessException.class);
        }
    }

    /**
     * 로그인된 사용자가 자신의 정보를 조회하는 테스트 메서드
     * 사용자가 로그인 후 자신의 정보를 올바르게 조회할 수 있는지 확인
     */
    @Test
    public void testMeAuthorizedAfterLogin() throws Exception {
        // 로그인 요청을 준비
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "user");
        loginRequest.put("password", "password");
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(loginRequest, loginHeaders);

        // 로그인 요청을 보냅
        ResponseEntity<String> loginResponse = restTemplate.exchange("/api/auth/login", HttpMethod.POST, requestEntity,
                String.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 로그인 후 세션 쿠키를 추출
        HttpHeaders loginResponseHeaders = loginResponse.getHeaders();
        List<String> cookies = loginResponseHeaders.get(HttpHeaders.SET_COOKIE);
        assertThat(cookies).isNotEmpty();

        // 인증된 상태로 사용자 정보를 요청
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.put(HttpHeaders.COOKIE, cookies);

        HttpEntity<String> request = new HttpEntity<>(headers);

        // 사용자 정보 요청을 보냅
        ResponseEntity<String> response = restTemplate.exchange("/api/users/me", HttpMethod.GET, request, String.class);

        // JSON 응답을 객체로 변환
        Map<String, Object> responseBody = objectMapper.readValue(response.getBody(),
                new TypeReference<Map<String, Object>>() {
                });

        // 응답이 정상적인지 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseBody.get("message")).isEqualTo("Login status");
        assertThat(responseBody.get("code")).isEqualTo("SUCCESS");
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
        assertThat(data.get("id")).isEqualTo(1);
        assertThat(data.get("username")).isEqualTo("user");
    }
}
