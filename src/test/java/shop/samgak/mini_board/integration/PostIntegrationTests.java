package shop.samgak.mini_board.integration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// 통합 테스트 클래스 - 게시글 관련된 기능에 대한 테스트 수행

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class PostIntegrationTests {

    private final String postsUrl = "/api/posts";
    private final String loginUrl = "/api/auth/login";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 로그인된 사용자가 게시글 목록에 접근하는 테스트 메서드
     * 세션 쿠키를 사용하여 인증된 사용자로서 게시글을 조회하는 시나리오를 테스트합니다.
     */
    @Test
    public void testAccessPostsAsLoggedInUser() throws Exception {
        // 로그인 후 세션 쿠키 획득
        String sessionCookie = loginUser("user", "password");

        // 게시글 접근을 위한 헤더 설정
        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.add("Cookie", sessionCookie);

        // 게시글 목록 요청
        ResponseEntity<String> postsResponse = restTemplate.exchange(postsUrl, HttpMethod.GET,
                new HttpEntity<>(postHeaders), String.class);
        // 응답 상태가 OK인지 확인
        assertThat(postsResponse.getStatusCode()).isEqualTo(OK);
    }

    /**
     * 로그인하지 않은 사용자가 게시글 목록에 접근하는 테스트 메서드
     * 인증 없이 게시글에 접근했을 때 UNAUTHORIZED 상태가 반환되는지 테스트합니다.
     */
    @Test
    public void testAccessPostsAsNotLoggedInUser() throws Exception {
        // 인증 없이 게시글 목록 요청
        ResponseEntity<String> postsResponse = restTemplate.getForEntity(postsUrl, String.class);
        // 응답 상태가 UNAUTHORIZED인지 확인
        assertThat(postsResponse.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    /**
     * 로그인된 사용자가 새로운 게시글을 생성하는 테스트 메서드
     * 인증된 사용자로서 새로운 게시글을 작성할 수 있는지 테스트합니다.
     */
    @Test
    public void testCreatePostAsLoggedInUser() throws Exception {
        // 로그인 후 세션 쿠키 획득
        String sessionCookie = loginUser("user", "password");

        // 게시글 생성 요청 생성
        MultiValueMap<String, String> postRequest = new LinkedMultiValueMap<>();
        postRequest.add("title", "New Post Title");
        postRequest.add("content", "Content of the new post");

        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.add("Cookie", sessionCookie);
        HttpEntity<MultiValueMap<String, String>> postEntity = new HttpEntity<>(postRequest, postHeaders);

        // 게시글 생성 요청 전송
        ResponseEntity<String> postResponse = restTemplate.postForEntity(postsUrl, postEntity, String.class);
        System.out.println(postResponse);
        // 응답 상태가 CREATED인지 확인
        assertThat(postResponse.getStatusCode()).isEqualTo(CREATED);
    }

    /**
     * 로그인하지 않은 상태에서 새로운 게시글을 생성하려는 테스트 메서드
     * 인증 없이 게시글을 작성했을 때 예외가 발생하는지 테스트합니다.
     */
    @Test
    public void testCreatePostAsNotLoggedInUser() throws Exception {
        // 게시글 생성 요청 생성
        MultiValueMap<String, String> postRequest = new LinkedMultiValueMap<>();
        postRequest.add("title", "Unauthorized Post Title");
        postRequest.add("content", "Content of the unauthorized post");

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> postEntity = new HttpEntity<>(postRequest, headers);

        // 인증 없이 게시글 생성 요청 시도
        try {
            restTemplate.postForEntity(postsUrl, postEntity, String.class);
            fail("Expected ResourceAccessException to be thrown"); // 예외가 발생해야 함
        } catch (RestClientException e) {
            assertThat(e).isInstanceOf(ResourceAccessException.class); // 예외 타입 확인
        }
    }

    /**
     * 로그인된 사용자가 본인의 게시글을 업데이트하는 테스트 메서드
     * 인증된 사용자로서 게시글 내용을 수정할 수 있는지 테스트합니다.
     */
    @Test
    public void testUpdatePostAsLoggedInUser() throws Exception {
        // 로그인 후 세션 쿠키 획득
        String sessionCookie = loginUser("user", "password");

        // 게시글 업데이트 요청 생성
        MultiValueMap<String, String> updateRequest = new LinkedMultiValueMap<>();
        updateRequest.add("title", "Updated Post Title");
        updateRequest.add("content", "Updated content of the post");

        HttpHeaders updateHeaders = new HttpHeaders();
        updateHeaders.add("Cookie", sessionCookie);
        HttpEntity<MultiValueMap<String, String>> updateEntity = new HttpEntity<>(updateRequest, updateHeaders);

        // 게시글 업데이트 요청 전송
        String updateUrl = postsUrl + "/{id}";
        ResponseEntity<String> updateResponse = restTemplate.exchange(updateUrl, HttpMethod.PUT, updateEntity,
                String.class, 1);

        // 응답 상태가 OK인지 확인
        assertThat(updateResponse.getStatusCode()).isEqualTo(OK);
    }

    /**
     * 로그인된 사용자가 본인의 게시글을 삭제하는 테스트 메서드
     * 인증된 사용자로서 게시글을 삭제할 수 있는지 테스트합니다.
     */
    @Test
    public void testDeletePostAsLoggedInUser() throws Exception {
        String sessionCookie = loginUser("user", "password");

        HttpHeaders deleteHeaders = new HttpHeaders();
        deleteHeaders.add("Cookie", sessionCookie);
        HttpEntity<String> deleteEntity = new HttpEntity<>(deleteHeaders);

        // 게시글 삭제 요청 전송
        String deleteUrl = postsUrl + "/{id}";
        ResponseEntity<String> deleteResponse = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, deleteEntity,
                String.class, 1);
        // 응답 상태가 OK인지 확인
        assertThat(deleteResponse.getStatusCode()).isEqualTo(OK);

        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("UPDATE posts SET DELETED_AT = NULL WHERE id = ?");
        statement.setInt(1, 1);
        statement.executeUpdate();
    }

    /**
     * 사용자 로그인을 시뮬레이션하고 세션 쿠키를 반환하는 메서드
     * 주어진 사용자명과 비밀번호로 로그인 요청을 보내고 세션 쿠키를 얻습니다.
     *
     * @param username 사용자명
     * @param password 비밀번호
     * @return 세션 쿠키 문자열
     */
    private String loginUser(String username, String password) throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);
        HttpEntity<String> requestEntity = getRequestEntity(loginRequest);

        // 로그인 요청 전송
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, requestEntity, String.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        return loginResponse.getHeaders().getFirst(SET_COOKIE); // 세션 쿠키 반환
    }

    /**
     * 요청 본문 반환
     * Map 객체를 JSON Body로 변환
     */
    private HttpEntity<String> getRequestEntity(Map<String, String> request) throws JsonProcessingException {
        String requestBody = objectMapper.writeValueAsString(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        return requestEntity;
    }
}
