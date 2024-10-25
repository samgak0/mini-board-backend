package shop.samgak.mini_board.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.exceptions.UserNotExistFoundException;
import shop.samgak.mini_board.exceptions.WrongPasswordException;
import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.user.entities.User;
import shop.samgak.mini_board.user.mapper.UserMapper;
import shop.samgak.mini_board.user.repositories.UserRepository;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws WrongPasswordException, UserNotExistFoundException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotExistFoundException(username));
        UserDTO userDTO = userMapper.userToUserDTO(user);

        MyUserDetails myUserDetails = new MyUserDetails(userDTO, user.getPassword());
        if (!passwordEncoder.matches(password, myUserDetails.getPassword())) {
            throw new WrongPasswordException(username);
        }
        myUserDetails.setPassword(null);

        return new UsernamePasswordAuthenticationToken(myUserDetails, null, myUserDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}