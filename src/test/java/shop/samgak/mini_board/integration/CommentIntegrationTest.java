package shop.samgak.mini_board.integration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CommentIntegrationTest {

    private final String commentGetUrl = "/api/posts/{postId}/comments";
    private final String commentCreateUrl = "/api/posts/{postId}/comments";
    private final String commentUpdateUrl = "/api/posts/{postId}/comments/{commentId}";
    private final String commentDeleteUrl = "/api/posts/{postId}/comments/{commentId}";

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

    @Test
    public void testAccessCommentsAsLoggedInUser() throws Exception {
        String sessionCookie = loginUser("user", "password");
        Long postId = 1L;

        ResponseEntity<String> commentsResponse = restClient.get()
                .uri(commentGetUrl, postId)
                .header(HttpHeaders.COOKIE, sessionCookie)
                .retrieve()
                .toEntity(String.class);

        assertThat(commentsResponse.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void testAccessCommentsAsNotLoggedInUser() throws Exception {
        Long postId = 1L;
        try {
            restClient.get()
                    .uri(commentGetUrl, postId)
                    .retrieve()
                    .toEntity(String.class);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(UNAUTHORIZED);
        }
    }

    @Test
    public void testCreateCommentAsLoggedInUser() throws Exception {
        Long postId = 1L;
        String sessionCookie = loginUser("user", "password");

        ResponseEntity<String> commentResponse = restClient.post()
                .uri(commentCreateUrl, postId)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.COOKIE, sessionCookie)
                .body(Map.of("content", "New Comment Content"))
                .retrieve()
                .toEntity(String.class);

        assertThat(commentResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void testCreateCommentAsNotLoggedInUser() throws Exception {
        Long commentId = 1L;
        Long postId = 1L;
        try {
            restClient.post()
                    .uri(commentDeleteUrl, postId, commentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("content", "Unauthorized Comment Content"))
                    .retrieve()
                    .toEntity(String.class);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(UNAUTHORIZED);
        }
    }

    @Test
    public void testUpdateCommentAsLoggedInUser() throws Exception {
        Long commentId = 1L;
        Long postId = 1L;
        String sessionCookie = loginUser("user", "password");

        ResponseEntity<String> updateResponse = restClient.put()
                .uri(commentUpdateUrl, postId, commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.COOKIE, sessionCookie)
                .body(Map.of("content", "Updated Comment Content"))
                .retrieve()
                .toEntity(String.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void testDeleteCommentAsLoggedInUser() throws Exception {
        Long commentId = 1L;
        Long postId = 1L;
        String sessionCookie = loginUser("user", "password");

        ResponseEntity<String> deleteResponse = restClient.delete()
                .uri(commentDeleteUrl, commentId, postId)
                .header(HttpHeaders.COOKIE, sessionCookie)
                .retrieve()
                .toEntity(String.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(OK);

        restoreCommentDelete();
    }

    public void restoreCommentDelete() throws SQLException {

        int id = 1;
        int postId = 1;
        int userId = 1;
        Timestamp createdAt = Timestamp.valueOf("2024-09-19 15:36:42.512476");
        Timestamp updatedAt = Timestamp.valueOf("2024-10-25 14:12:39.910166");
        String content = "첫 번째 댓글입니다.";

        // SQL INSERT 쿼리
        String sql = "INSERT INTO COMMENTS " +
                "(ID, POST_ID, USER_ID, CREATED_AT, PARENT_COMMENT_ID, UPDATED_AT, CONTENT) " +
                "VALUES (?, ?, ?, ?, NULL, ?, ?)";

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            // 각 필드에 지역 변수 값 설정
            statement.setInt(1, id);
            statement.setInt(2, postId);
            statement.setInt(3, userId);
            statement.setTimestamp(4, createdAt);
            statement.setTimestamp(5, updatedAt);
            statement.setString(6, content);

            // SQL 쿼리 실행
            statement.executeUpdate();
        }
    }

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
