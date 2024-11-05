package shop.samgak.mini_board.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.exceptions.UserNotFoundException;
import shop.samgak.mini_board.exceptions.WrongPasswordException;
import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.user.entities.User;
import shop.samgak.mini_board.user.mapper.UserMapper;
import shop.samgak.mini_board.user.repositories.UserRepository;

/**
 * CustomAuthenticationProvider는 Spring Security의 AuthenticationProvider를 구현하여
 * 사용자 인증 로직을 커스터마이징
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * PasswordEncoder 설정 메서드
     * 
     * @param passwordEncoder 비밀번호를 암호화하고 검증하는 인코더
     */
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 실제 인증을 처리하는 메서드
     * 
     * @param authentication 인증 요청 객체로, 사용자명과 비밀번호를 포함함
     * @return 인증이 성공한 경우 Authentication 객체 반환
     * @throws UserNotFoundException  사용자 이름이 존재하지 않을 때 발생
     * @throws WrongPasswordException 비밀번호가 일치하지 않을 때 발생
     */
    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    return new UserNotFoundException(username);
                });

        UserDTO userDTO = userMapper.toDTO(user);
        log.debug("userDTO = ", userDTO);

        MyUserDetails myUserDetails = new MyUserDetails(userDTO, user.getPassword());
        if (!passwordEncoder.matches(password, myUserDetails.getPassword())) {
            throw new WrongPasswordException(username);
        }
        // 인증 후 비밀번호는 null로 설정하여 보안 강화함
        myUserDetails.setPassword(null);

        return new UsernamePasswordAuthenticationToken(myUserDetails, null, myUserDetails.getAuthorities());
    }

    /**
     * 이 AuthenticationProvider가 특정 인증 유형을 지원하는지 여부 반환
     * 
     * @param authentication 인증 객체의 클래스
     * @return 지원 여부 (UsernamePasswordAuthenticationToken이면 true 반환)
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
