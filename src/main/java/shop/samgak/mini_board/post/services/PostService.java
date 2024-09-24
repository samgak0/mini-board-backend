package shop.samgak.mini_board.post.services;

import java.util.List;

import shop.samgak.mini_board.post.dto.PostDTO;

public interface PostService {
    List<PostDTO> getAll();

    PostDTO getPostById(Long id);
}
