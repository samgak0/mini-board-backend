package shop.samgak.mini_board.post.services;

import java.util.List;
import java.util.Optional;

import shop.samgak.mini_board.post.dto.PostFileDTO;

public interface PostFileService {
    List<PostFileDTO> getItemByPost(Long postId);

    Optional<PostFileDTO> getItem(Long postFileId, Long postId);

    Long writePostFileInfo(Long postId, String originalFileName, String filename, String contentType,
            long fileSize);
}
