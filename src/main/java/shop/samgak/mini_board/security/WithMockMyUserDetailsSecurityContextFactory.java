package shop.samgak.mini_board.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import shop.samgak.mini_board.user.dto.UserDTO;

/**
 * WithMockMyUserDetailsSecurityContextFactory는 @WithMockMyUserDetails 어노테이션을
 * 처리하여
 * 테스트 환경에서 사용될 SecurityContext를 생성함
 */
public class WithMockMyUserDetailsSecurityContextFactory implements WithSecurityContextFactory<WithMockMyUserDetails> {

    /**
     * 주어진 @WithMockMyUserDetails 어노테이션을 기반으로 SecurityContext 생성
     * 
     * @param annotation @WithMockMyUserDetails 어노테이션
     * @return 생성된 SecurityContext
     */
    @Override
    public SecurityContext createSecurityContext(WithMockMyUserDetails annotation) {
        String username = annotation.username(); // 어노테이션에서 사용자 이름 가져옴
        long id = annotation.id(); // 어노테이션에서 사용자 ID 가져옴

        UserDTO userDTO = new UserDTO(id, username);
        MyUserDetails myUserDetails = new MyUserDetails(userDTO, null);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        // 인증 정보 생성 및 설정
        Authentication authentication = new UsernamePasswordAuthenticationToken(myUserDetails, null,
                myUserDetails.getAuthorities());
        context.setAuthentication(authentication);

        return context;
    }
}
