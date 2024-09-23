package shop.samgak.mini_board.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.dto.UserDTO;
import shop.samgak.mini_board.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    final UserRepository userRepository;
    final ModelMapper modelMapper;

    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }
}
