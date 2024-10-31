package shop.samgak.mini_board.user.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.user.entities.User;
import shop.samgak.mini_board.user.mapper.UserMapper;
import shop.samgak.mini_board.user.repositories.UserRepository;

// UserService 구현체 - 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;
    final UserMapper userMapper;
    final PasswordEncoder passwordEncoder;

    /**
     * 모든 사용자 정보를 가져오는 메서드
     * 
     * @return UserDTO 리스트
     */
    @Override
    public List<UserDTO> getAll() {
        log.debug("모든 사용자 정보를 가져옴");
        return userRepository.findAll().stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 사용자를 저장하는 메서드
     * 
     * @param username 사용자 이름
     * @param email    사용자 이메일
     * @param password 사용자 비밀번호
     * @return 저장된 사용자의 ID
     */
    @Override
    public Long save(String username, String email, String password) {
        log.info("새로운 사용자 저장 - 사용자명: {}", username);
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setHashedPassword(passwordEncoder, password); // 비밀번호를 해시하여 설정
        user = userRepository.save(user); // 사용자 저장
        return user.getId();
    }

    /**
     * 사용자 이름이 존재하는지 확인하는 메서드
     * 
     * @param username 사용자 이름
     * @return 사용자 이름 존재 여부
     */
    @Override
    public boolean existUsername(String username) {
        log.debug("사용자명 존재 여부 확인 - 사용자명: {}", username);
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * 이메일이 존재하는지 확인하는 메서드
     * 
     * @param email 사용자 이메일
     * @return 이메일 존재 여부
     */
    @Override
    public boolean existEmail(String email) {
        log.debug("이메일 존재 여부 확인 - 이메일: {}", email);
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * 사용자의 비밀번호를 변경하는 메서드
     * 
     * @param username    사용자 이름
     * @param newPassword 새 비밀번호
     * @throws IllegalArgumentException 사용자 이름이 존재하지 않을 경우 예외 발생
     */
    @Override
    public void changePassword(String username, String newPassword) throws IllegalArgumentException {
        log.info("사용자 비밀번호 변경 - 사용자명: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Username is not available"));
        user.setHashedPassword(passwordEncoder, newPassword); // 새로운 비밀번호 해시하여 설정
        userRepository.save(user); // 변경된 사용자 정보 저장
    }
}
