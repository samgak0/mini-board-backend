package shop.samgak.mini_board.post.services;

import java.util.List;

import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.user.dto.UserDTO;

public interface PostService {
    List<PostDTO> getTop10();

    PostDTO getPostById(Long id);

    Long create(String title, String body, UserDTO userDTO);

    void update(Long id, String title, String content, UserDTO userDTO);

    void delete(Long id, UserDTO userDTO);

    boolean existsById(Long id);
}
