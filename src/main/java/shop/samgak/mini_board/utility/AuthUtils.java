package shop.samgak.mini_board.utility;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import shop.samgak.mini_board.user.entities.User;

public class AuthUtils {
    public static Optional<User> getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
        return (principal instanceof User userDetails) ? Optional.of(userDetails) : null;
        }
        return Optional.empty();
    }

    public static Optional<User> getCurrentUser() {
        return AuthUtils.getCurrentUser(SecurityContextHolder.getContext().getAuthentication());
    }
}
