package shop.samgak.mini_board.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.entities.User;
import shop.samgak.mini_board.repositories.UserRepository;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    final UserRepository repository;

    @GetMapping
    public List<User> getAllPosts() {
        return repository.findAll();
    }
}