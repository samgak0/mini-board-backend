package shop.samgak.mini_board.user.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.user.entities.User;
import shop.samgak.mini_board.user.mapper.UserMapper;
import shop.samgak.mini_board.user.repositories.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;
    final UserMapper userMapper;
    final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long save(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setHashedPassword(passwordEncoder, password);
        user = userRepository.save(user);
        return user.getId();
    }

    @Override
    public boolean existUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public boolean existEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public Optional<UserDetails> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            return (principal instanceof UserDetails userDetails) ? Optional.of(userDetails) : null;
        }
        return Optional.empty();
    }

    @Override
    public boolean isLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() != null;
    }

    @Override
    public void changePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Username is not available"));
        user.setHashedPassword(passwordEncoder, newPassword);
        userRepository.save(user);
    }
}
