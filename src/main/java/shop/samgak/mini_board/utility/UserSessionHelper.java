package shop.samgak.mini_board.utility;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.user.entities.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSessionHelper {
    public static final String SESSION_USER_KEY = "user";

    private final ObjectMapper objectMapper;

    public void setUserSession(User user, HttpSession session) throws JsonProcessingException {
        session.setAttribute(SESSION_USER_KEY, objectMapper.writeValueAsString(user));
    }

    public Optional<User> getCurrentUserFromSession(HttpSession session) throws JsonProcessingException {
        if (session == null)
            return Optional.empty();

        String userJson = (String) session.getAttribute(SESSION_USER_KEY);
        if (userJson == null || userJson.isEmpty())
            return Optional.empty();

        return Optional.of(objectMapper.readValue(userJson, User.class));
    }
}
