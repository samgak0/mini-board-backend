package shop.samgak.mini_board.user.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
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
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long save(String username, String email, String password) {
        User user = new User(null, username, email, passwordEncoder.encode(password), Instant.now(), Instant.now(),
                null);
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
    public void changePassword(String username, String newPassword) throws IllegalArgumentException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username : [" + username + "]"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
