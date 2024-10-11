package shop.samgak.mini_board.utility;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.security.MyUserDetails;
import shop.samgak.mini_board.user.dto.UserDTO;

@Slf4j
public class AuthUtils {
    public static Optional<UserDTO> getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof MyUserDetails myUserDetails)) {
            return Optional.empty();
        }
        return Optional.of(myUserDetails.getUserDTO());
    }

    public static Optional<UserDTO> getCurrentUser() {
        return getCurrentUser(SecurityContextHolder.getContext().getAuthentication());
    }
}