package shop.samgak.mini_board.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}