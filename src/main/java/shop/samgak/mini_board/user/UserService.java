<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/user/services/UserService.java
package shop.samgak.mini_board.user.services;
========
package shop.samgak.mini_board.user;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/user/UserService.java

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/user/services/UserService.java
import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.user.repositories.UserRepository;
========
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/user/UserService.java

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
