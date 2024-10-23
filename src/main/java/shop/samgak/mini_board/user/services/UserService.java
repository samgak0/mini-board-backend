package shop.samgak.mini_board.user.services;

import java.util.List;

import shop.samgak.mini_board.user.dto.UserDTO;

public interface UserService {
    List<UserDTO> getAll();

    Long save(String username, String email, String password);

    boolean existUsername(String username);

    boolean existEmail(String email);

    void changePassword(String username, String newPassword) throws IllegalArgumentException;

}
