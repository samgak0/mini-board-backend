package shop.samgak.mini_board.utility;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.exceptions.UserNotLoginException;
import shop.samgak.mini_board.security.MyUserDetails;
import shop.samgak.mini_board.user.dto.UserDTO;

/**
 * 인증 관련 유틸리티 클래스
 * 사용자의 인증 정보와 세션 관리 기능을 제공
 */
@Slf4j
public class AuthUtils {

    /**
     * SecurityContext를 세션에 저장하는 메서드
     * 제공된 SecurityContext를 사용자의 HTTP 세션에 저장합니다.
     *
     * @param securityContext 저장할 SecurityContext
     * @param session         SecurityContext를 저장할 HTTP 세션
     */
    public static void saveSessionSecurityContext(SecurityContext securityContext, HttpSession session) {
        // HttpSessionSecurityContextRepository에서 사용되는 키를 사용하여 SecurityContext를 세션에 저장
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
    }

    /**
     * 사용자가 로그인했는지 확인하는 메서드
     * 사용자의 HTTP 세션에 SecurityContext가 저장되어 있는지 여부를 확인합니다.
     *
     * @return 사용자가 로그인한 상태이면 true, 그렇지 않으면 false
     */
    public static boolean checkLogin() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return false;
        }

        Authentication authentication = context.getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        }

        boolean isLoggedIn = authentication.getPrincipal() instanceof MyUserDetails;
        return isLoggedIn;
    }

    /**
     * 현재 로그인된 사용자의 정보를 가져오는 메서드
     * SecurityContext에서 사용자 정보를 조회하여 UserDTO로 반환합니다.
     *
     * @return 현재 로그인된 사용자의 UserDTO 객체
     * @throws UserNotLoginException 사용자가 로그인하지 않은 경우 예외 발생
     */
    public static UserDTO getCurrentUser() throws UserNotLoginException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication() != null) {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication.getPrincipal() != null) {
                MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
                return myUserDetails.getUserDTO();
            } else {
                log.warn("Principal not found in credentials.");
                throw new UserNotLoginException();
            }
        } else {
            throw new UserNotLoginException();
        }
    }
}
