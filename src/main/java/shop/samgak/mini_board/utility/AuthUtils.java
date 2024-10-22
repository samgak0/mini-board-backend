package shop.samgak.mini_board.utility;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.user.dto.UserDTO;

@Slf4j
public class AuthUtils {

    /**
     * Saves the SecurityContext to the session.
     * This method stores the provided SecurityContext in the user's HTTP session.
     * 
     * @param securityContext the security context to be saved
     * @param session         the HTTP session to store the security context
     */
    public static void saveSessionSecurityContext(SecurityContext securityContext, HttpSession session) {
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
    }

    /**
     * Retrieves the current user from the session as an Optional<UserDTO>.
     * This method also restores the SecurityContext from the session.
     * 
     * @param session the HTTP session from which the user information is retrieved
     * @return an Optional containing the UserDTO if available, otherwise an empty
     *         Optional
     */
    public static Optional<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof UserDTO) {
            return Optional.of((UserDTO) authentication.getDetails());
        }
        return Optional.empty();
    }
}
