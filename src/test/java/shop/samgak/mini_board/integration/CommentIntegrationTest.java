package shop.samgak.mini_board.integration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
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
import static org.springframework.http.HttpHeaders.SET_COOKIE;
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
public class CommentIntegrationTest {

    private final String commentsUrl = "/api/posts/{postId}/comments";
    private final String loginUrl = "/api/auth/login";

    @Autowired
    private DataSource dataSource;

    private RestClient restClient;

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

    @Test
    public void testAccessCommentsAsLoggedInUser() throws Exception {
        String sessionCookie = loginUser("user", "password");
        Long postId = 1L;

        ResponseEntity<String> commentsResponse = restClient.get()
                .uri(commentsUrl, postId)
                .header("Cookie", sessionCookie)
                .retrieve()
                .toEntity(String.class);

        assertThat(commentsResponse.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void testAccessCommentsAsNotLoggedInUser() throws Exception {
        Long postId = 1L;
        try {
            restClient.get()
                    .uri(commentsUrl, postId)
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
        Map<String, String> commentRequest = new HashMap<>();
        commentRequest.put("content", "New Comment Content");

        ResponseEntity<String> commentResponse = restClient.post()
                .uri(commentsUrl, postId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Cookie", sessionCookie)
                .body(commentRequest)
                .retrieve()
                .toEntity(String.class);

        assertThat(commentResponse.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    public void testCreateCommentAsNotLoggedInUser() throws Exception {
        Map<String, String> commentRequest = new HashMap<>();
        commentRequest.put("content", "Unauthorized Comment Content");

        try {
            restClient.post()
                    .uri(commentsUrl, 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(commentRequest)
                    .retrieve()
                    .toEntity(String.class);
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(UNAUTHORIZED);
        }
    }

    @Test
    public void testUpdateCommentAsLoggedInUser() throws Exception {
        String sessionCookie = loginUser("user", "password");

        Map<String, String> updateRequest = new HashMap<>();
        updateRequest.put("content", "Updated Comment Content");

        ResponseEntity<String> updateResponse = restClient.put()
                .uri(commentsUrl + "/{commentId}", 1, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Cookie", sessionCookie)
                .body(updateRequest)
                .retrieve()
                .toEntity(String.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void testDeleteCommentAsLoggedInUser() throws Exception {
        String sessionCookie = loginUser("user", "password");

        ResponseEntity<String> deleteResponse = restClient.delete()
                .uri(commentsUrl + "/{commentId}", 1, 1)
                .header("Cookie", sessionCookie)
                .retrieve()
                .toEntity(String.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(OK);

        restoreCommentDelete();
    }

    private void restoreCommentDelete() throws SQLException {
        String sql = "INSERT INTO SAMGAK_TEST.POSTS " +
                "(ID, USER_ID, TITLE, CONTENT, CREATED_AT, UPDATED_AT, VIEW_COUNT, DELETED_AT) " +
                "VALUES (?, ?, ?, TO_CLOB(?), ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, 1);
            statement.setInt(2, 1);
            statement.setString(3, "Updated Post Title");
            statement.setString(4, "Updated content of the post");
            statement.setTimestamp(5, java.sql.Timestamp.valueOf("2024-09-26 17:58:39.494061"));
            statement.setTimestamp(6, java.sql.Timestamp.valueOf("2024-10-30 04:37:58.257705"));
            statement.setInt(7, 11);
            statement.setNull(8, java.sql.Types.TIMESTAMP);

            statement.executeUpdate();
        }
    }

    private String loginUser(String username, String password) throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);

        ResponseEntity<String> loginResponse = restClient.post()
                .uri(loginUrl)
                .body(loginRequest)
                .retrieve()
                .toEntity(String.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        return loginResponse.getHeaders().getFirst(SET_COOKIE);
    }
}
