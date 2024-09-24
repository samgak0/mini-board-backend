package shop.samgak.mini_board.comment.mapper;

import org.mapstruct.Mapper;

import shop.samgak.mini_board.comment.dto.CommentDTO;
import shop.samgak.mini_board.comment.entities.Comment;
import shop.samgak.mini_board.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface CommentMapper {
    CommentDTO toDTO(Comment comment);

    Comment fromDTO(CommentDTO commentDTO);
}
