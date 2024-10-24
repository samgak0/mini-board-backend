package shop.samgak.mini_board.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import shop.samgak.mini_board.user.dto.UserDTO;

public class WithMockMyUserDetailsSecurityContextFactory implements WithSecurityContextFactory<WithMockMyUserDetails> {

    @Override
    public SecurityContext createSecurityContext(WithMockMyUserDetails annotation) {
        String username = annotation.username();
        long id = annotation.id();
        UserDTO userDTO = new UserDTO(id, username);
        MyUserDetails myUserDetails = new MyUserDetails(userDTO, null);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken(myUserDetails, null,
                myUserDetails.getAuthorities());
        context.setAuthentication(authentication);
        return context;
    }
}