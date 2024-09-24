package shop.samgak.mini_board;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.http.HttpStatus.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag("integration")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
public class MiniBoardApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String loginUrl = "/api/users/login"; // 로그인 URL
    private final String postsUrl = "/api/posts"; // 보호된 URL

    @Test
    public void testLoginAndAccessPosts() {
        // 로그인 요청을 위한 HttpEntity 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = "username=user&password=password"; // 로그인 정보
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // 로그인 요청
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, requestEntity, String.class);

        // 로그인 성공 여부 검증
        assertEquals(OK, loginResponse.getStatusCode());

        // 로그인 후 쿠키를 사용하여 보호된 리소스 접근
        HttpHeaders responseHeaders = loginResponse.getHeaders();
        String sessionId = responseHeaders.getFirst("Set-Cookie");

        // 쿠키 설정
        HttpHeaders headersForPosts = new HttpHeaders();
        headersForPosts.add("Cookie", sessionId);

        // 보호된 리소스 접근
        HttpEntity<String> postsRequestEntity = new HttpEntity<>(headersForPosts);
        ResponseEntity<String> postsResponse = restTemplate.exchange(postsUrl, HttpMethod.GET, postsRequestEntity,
                String.class);

        // 게시글 접근 권한 확인
        assertEquals(OK, postsResponse.getStatusCode());
        // 추가적인 검증 로직
    }
}
