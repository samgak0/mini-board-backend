package shop.samgak.mini_board.post.services;

import java.util.List;

import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.exceptions.UnauthorizedActionException;
import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.user.dto.UserDTO;

public interface PostService {
    List<PostDTO> getTop10();

    PostDTO getPostById(Long id) throws ResourceNotFoundException;

    Long create(String title, String content, UserDTO userDTO);

    void update(Long id, String title, String content, UserDTO userDTO)
            throws ResourceNotFoundException, UnauthorizedActionException;

    void delete(Long id, UserDTO userDTO) throws ResourceNotFoundException, UnauthorizedActionException;

    boolean existsById(Long id);
}
