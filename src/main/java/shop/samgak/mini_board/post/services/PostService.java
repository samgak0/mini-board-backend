package shop.samgak.mini_board.post.services;

import java.util.List;

import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.user.entities.User;

public interface PostService {
    List<PostDTO> getAll();

    PostDTO getPostById(Long id);

    Long create(String title, String body, User user);
}
