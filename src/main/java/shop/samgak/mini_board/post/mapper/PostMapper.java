package shop.samgak.mini_board.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.post.entities.Post;
import shop.samgak.mini_board.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface PostMapper {
    @Mapping(source = "created_at", target = "createdAt")
    @Mapping(source = "updated_at", target = "updatedAt")
    PostDTO postToPostDTO(Post post);

    @Mapping(source = "createdAt", target = "created_at")
    @Mapping(source = "updatedAt", target = "updated_at")
    Post postDTOToPost(PostDTO postDTO);
}
