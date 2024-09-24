package shop.samgak.mini_board.post.services;

import java.util.List;

import shop.samgak.mini_board.post.dto.PostLikeDTO;

public interface PostLikeService {
    List<PostLikeDTO> getAll();
}
