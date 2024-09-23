<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/user/repositories/UserRepository.java
package shop.samgak.mini_board.user.repositories;
========
package shop.samgak.mini_board.user;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/user/UserRepository.java

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/user/repositories/UserRepository.java
import shop.samgak.mini_board.user.entities.User;

========
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/user/UserRepository.java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}