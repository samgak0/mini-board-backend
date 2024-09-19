package shop.samgak.mini_board.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.entities.User;
import shop.samgak.mini_board.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    final UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }
}
