package shop.samgak.mini_board.post.mapper;

import org.mapstruct.Mapper;

import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.post.entities.Post;
import shop.samgak.mini_board.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface PostMapper {
    PostDTO toDTO(Post post);

    Post fromDTO(PostDTO postDTO);
}
