package shop.samgak.mini_board.integration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class PostIntegrationTests {

    private final String postsUrl = "/api/posts";
    private final String loginUrl = "/api/auth/login";

    @Autowired
    private DataSource dataSource;

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
     * 로그인된 사용자가 게시글 목록에 접근하는 테스트 메서드
     * 세션 쿠키를 사용하여 인증된 사용자로서 게시글을 조회하는 시나리오를 테스트합니다.
     */
    @Test
    public void testAccessPostsAsLoggedInUser() throws Exception {
        String sessionCookie = loginUser("user", "password");

        ResponseEntity<String> postsResponse = restClient.get()
                .uri(postsUrl)
                .header(HttpHeaders.COOKIE, sessionCookie)
                .retrieve()
                .toEntity(String.class);

        assertThat(postsResponse.getStatusCode()).isEqualTo(OK);
    }

    /**
     * 로그인하지 않은 사용자가 게시글 목록에 접근하는 테스트 메서드
     * 인증 없이 게시글에 접근했을 때 UNAUTHORIZED 상태가 반환되는지 테스트합니다.
     */
    @Test
    public void testAccessPostsAsNotLoggedInUser() throws Exception {
        try {
            restClient.get()
                    .uri(postsUrl)
                    .retrieve()
                    .toEntity(String.class);
            fail("Expected HttpClientErrorException to be thrown");
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(UNAUTHORIZED);
        }
    }

    /**
     * 로그인된 사용자가 새로운 게시글을 생성하는 테스트 메서드
     * 인증된 사용자로서 새로운 게시글을 작성할 수 있는지 테스트합니다.
     */
    @Test
    public void testCreatePostAsLoggedInUser() throws Exception {
        String sessionCookie = loginUser("user", "password");
        String title = "New Post Title";
        String content = "Content of the new post";

        ResponseEntity<String> postResponse = restClient.post()
                .uri(postsUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.COOKIE, sessionCookie)
                .body(Map.of("title", title, "content", content))
                .retrieve()
                .toEntity(String.class);

        assertThat(postResponse.getStatusCode()).isEqualTo(CREATED);
    }

    /**
     * 로그인하지 않은 상태에서 새로운 게시글을 생성하려는 테스트 메서드
     * 인증 없이 게시글을 작성했을 때 예외가 발생하는지 테스트합니다.
     */
    @Test
    public void testCreatePostAsNotLoggedInUser() throws Exception {
        String title = "Unauthorized Post Title";
        String content = "Content of the unauthorized post";

        try {
            restClient.post()
                    .uri(postsUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("title", title, "content", content))
                    .retrieve()
                    .toEntity(String.class);
            fail("Expected HttpClientErrorException to be thrown");
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(UNAUTHORIZED);
        }
    }

    /**
     * 로그인된 사용자가 본인의 게시글을 업데이트하는 테스트 메서드
     * 인증된 사용자로서 게시글 내용을 수정할 수 있는지 테스트합니다.
     */
    @Test
    public void testUpdatePostAsLoggedInUser() throws Exception {
        String sessionCookie = loginUser("user", "password");
        String title = "Updated Post Title";
        String content = "Updated content of the post";

        ResponseEntity<String> updateResponse = restClient.put()
                .uri(postsUrl + "/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.COOKIE, sessionCookie)
                .body(Map.of("title", title, "content", content))
                .retrieve()
                .toEntity(String.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(OK);
    }

    /**
     * 로그인된 사용자가 본인의 게시글을 삭제하는 테스트 메서드
     * 인증된 사용자로서 게시글을 삭제할 수 있는지 테스트합니다.
     */
    @Test
    public void testDeletePostAsLoggedInUser() throws Exception {
        String sessionCookie = loginUser("user", "password");

        ResponseEntity<String> deleteResponse = restClient.delete()
                .uri(postsUrl + "/{id}", 1)
                .header(HttpHeaders.COOKIE, sessionCookie)
                .retrieve()
                .toEntity(String.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(OK);

        restorePostDelete();
    }

    private void restorePostDelete() throws SQLException {
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

        ResponseEntity<String> loginResponse = restClient.post()
                .uri(loginUrl)
                .body(Map.of("username", username, "password", password))
                .retrieve()
                .toEntity(String.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        return loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
    }
}
