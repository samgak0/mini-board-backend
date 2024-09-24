package shop.samgak.mini_board.post.services;

import java.util.List;

import shop.samgak.mini_board.post.dto.PostFileDTO;

public interface PostFileService {
    List<PostFileDTO> getAll();
}
