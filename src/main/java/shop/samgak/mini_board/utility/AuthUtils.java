package shop.samgak.mini_board.utility;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.security.MyUserDetails;
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
     * Checks if the session has a SecurityContext.
     * This method checks whether the user's HTTP session contains a stored
     * SecurityContext.
     * 
     * @param session the HTTP session to be checked
     * @return true if the session has a SecurityContext, false otherwise
     */
    public static boolean checkSessionHasSecurityContext(HttpSession session) {
        return session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY) != null;
    }

    /**
     * Retrieves the current user from the session as an Optional<UserDTO>.
     * This method also restores the SecurityContext from the session.
     * 
     * @param session the HTTP session from which the user information is retrieved
     * @return an Optional containing the UserDTO if available, otherwise an empty
     *         Optional
     */
    public static Optional<UserDTO> getCurrentUserFromSession(HttpSession session) {
        if (session == null)
            return Optional.empty();

        restoreSecurityContext(session);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null && securityContext.getAuthentication() != null) {
            Authentication authentication = securityContext.getAuthentication();
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
            return Optional.of(myUserDetails.getUserDTO());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Restores the SecurityContext from the session.
     * This method retrieves the SecurityContext from the user's HTTP session and
     * sets it in the SecurityContextHolder.
     * 
     * @param session the HTTP session from which the SecurityContext is restored
     * @return the restored SecurityContext
     */
    public static SecurityContext restoreSecurityContext(HttpSession session) {
        SecurityContext securityContext = (SecurityContext) session
                .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        SecurityContextHolder.setContext(securityContext);
        return securityContext;
    }
}
