package shop.samgak.mini_board.comment.mapper;

import org.mapstruct.Mapper;

import shop.samgak.mini_board.comment.dto.CommentFileDTO;
import shop.samgak.mini_board.comment.entities.CommentFile;
import shop.samgak.mini_board.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface CommentFileMapper {
    CommentFileDTO toDTO(CommentFile commentFile);

    CommentFile fromDTO(CommentFileDTO commentFileDTO);
}
