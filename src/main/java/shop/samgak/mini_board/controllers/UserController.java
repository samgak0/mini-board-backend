package shop.samgak.mini_board.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.dto.UserDTO;
import shop.samgak.mini_board.services.UserService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    final UserService userService;

    @GetMapping("/api/users")
    public List<UserDTO> getAllUser() {
        return userService.getAll();
    }
}
