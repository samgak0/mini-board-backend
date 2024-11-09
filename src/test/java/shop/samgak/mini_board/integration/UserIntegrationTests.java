package shop.samgak.mini_board.integration;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import shop.samgak.mini_board.user.services.UserService;

/**
 * 통합 테스트 클래스 - 사용자 관련 기능을 테스트
 * 이 클래스는 사용자 인증 및 보호된 자원 접근에 대한 테스트를 수행
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserIntegrationTests {

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private RestClient restClient;

    @LocalServerPort
    private int port;

    @Value("${app.hostname:localhost}")
    private String hostname;

    @Value("${app.secure:false}")
    private boolean secure;

    @BeforeEach
    public void setup() {
        String baseUrl = (secure ? "https" : "http") + "://" + hostname + ":" + port;
        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * 이미 사용 중인 이메일로 회원가입을 시도할 때의 테스트
     * 해당 이메일이 이미 사용 중이라는 가정 하에 회원가입 요청을 테스트
     */
    @Test
    public void testRegisterEmailAlreadyUsed() throws Exception {
        String username = "newUser";
        String email = "existing@example.com";
        String password = "password123";

        when(userService.existEmail(email)).thenReturn(true);
        when(userService.existUsername(username)).thenReturn(false);

        try {
            restClient.post()
                    .uri("/api/users/register")
                    .body(Map.of("username", username, "email", email, "password", password))
                    .retrieve()
                    .toEntity(String.class);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

            Map<String, Object> responseBody = objectMapper.readValue(e.getResponseBodyAsString(),
                    new TypeReference<Map<String, Object>>() {
                    });

            assertThat(responseBody.get("message")).isEqualTo("Email is already in use");
            assertThat(responseBody.get("code")).isEqualTo("USED");
        }
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

        when(userService.existUsername(username)).thenReturn(true);
        when(userService.existEmail(email)).thenReturn(false);

        try {
            restClient.post()
                    .uri("/api/users/register")
                    .body(Map.of("username", username, "email", email, "password", password))
                    .retrieve()
                    .toEntity(String.class);
            fail("Expected HttpClientErrorException to be thrown");
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

            Map<String, Object> responseBody = objectMapper.readValue(e.getResponseBodyAsString(),
                    new TypeReference<Map<String, Object>>() {
                    });

            assertThat(responseBody.get("message")).isEqualTo("Username is already in use");
            assertThat(responseBody.get("code")).isEqualTo("USED");
        }
    }

    /**
     * 사용자 이름이 누락된 상태로 회원가입을 시도할 때의 테스트
     * 사용자 이름이 제공되지 않은 경우 API에서 오류가 발생하는지 확인
     */
    @Test
    public void testRegisterMissingUsername() throws Exception {
        String email = "newuser@example.com";
        String password = "password123";

        try {
            restClient.post()
                    .uri("/api/users/register")
                    .body(Map.of("email", email, "password", password))
                    .retrieve()
                    .toEntity(String.class);
            fail("Expected HttpClientErrorException to be thrown");
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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

        try {
            restClient.post()
                    .uri("/api/users/register")
                    .body(Map.of("username", username, "email", email))
                    .retrieve()
                    .toEntity(String.class);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 로그인하지 않은 상태에서 사용자 정보를 요청할 때의 테스트.
     * 인증되지 않은 사용자가 보호된 자원에 접근할 때의 동작을 테스트
     */
    @Test
    public void testMeUnauthorized() {
        try {
            restClient.get()
                    .uri("/api/users/me")
                    .retrieve()
                    .toEntity(String.class);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 로그인된 사용자가 자신의 정보를 조회하는 테스트 메서드
     * 사용자가 로그인 후 자신의 정보를 올바르게 조회할 수 있는지 확인
     */
    @Test
    public void testMeAuthorizedAfterLogin() throws Exception {
        String username = "user";
        String password = "password";

        ResponseEntity<String> loginResponse = restClient.post()
                .uri("/api/auth/login")
                .body(Map.of("username", username, "password", password))
                .retrieve()
                .toEntity(String.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 로그인 후 세션 쿠키를 추출
        HttpHeaders loginResponseHeaders = loginResponse.getHeaders();
        List<String> cookies = loginResponseHeaders.get(HttpHeaders.SET_COOKIE);
        assertThat(cookies).isNotEmpty();

        // 인증된 상태로 사용자 정보를 요청
        ResponseEntity<String> response = restClient.get()
                .uri("/api/users/me")
                .header(HttpHeaders.COOKIE, String.join("; ", cookies))
                .retrieve()
                .toEntity(String.class);

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
