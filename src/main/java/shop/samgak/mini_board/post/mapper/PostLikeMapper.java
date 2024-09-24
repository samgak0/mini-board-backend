package shop.samgak.mini_board.post.mapper;

import org.mapstruct.Mapper;

import shop.samgak.mini_board.post.dto.PostLikeDTO;
import shop.samgak.mini_board.post.entities.PostLike;
import shop.samgak.mini_board.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = { UserMapper.class, PostMapper.class })
public interface PostLikeMapper {
    PostLikeDTO postLikeToPostLikeDTO(PostLike postLike);

    PostLike postLikeDTOToPostLike(PostLikeDTO postLikeDTO);
}
