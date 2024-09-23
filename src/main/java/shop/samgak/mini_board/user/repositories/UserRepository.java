package shop.samgak.mini_board.user.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.samgak.mini_board.user.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}