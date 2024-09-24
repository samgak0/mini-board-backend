package shop.samgak.mini_board.user.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.user.entities.User;
import shop.samgak.mini_board.user.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final UserRepository userRepository;
    final ModelMapper modelMapper;

    @Override
    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Long save(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
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
}
