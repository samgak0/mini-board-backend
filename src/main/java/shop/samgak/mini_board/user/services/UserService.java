package shop.samgak.mini_board.user.services;

import java.util.List;
import java.util.Optional;

import shop.samgak.mini_board.user.dto.UserDTO;

public interface UserService {
    List<UserDTO> getAll();
    Long save(String username, String email, String password);
    boolean existUsername(String username);
    boolean existEmail(String email);
    boolean isLogin();
    Optional<UserDTO> getCurrentUser();
    void changePassword(String username, String newPassword);

}
