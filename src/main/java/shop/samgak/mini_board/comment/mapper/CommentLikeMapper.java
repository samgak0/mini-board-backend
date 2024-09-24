package shop.samgak.mini_board.comment.mapper;

import org.mapstruct.Mapper;

import shop.samgak.mini_board.comment.dto.CommentLikeDTO;
import shop.samgak.mini_board.comment.entities.CommentLike;
import shop.samgak.mini_board.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface CommentLikeMapper {
    CommentLikeDTO toDTO(CommentLike commentLike);

    CommentLike fromDTO(CommentLikeDTO commentLikeDTO);
}
