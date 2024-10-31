package shop.samgak.mini_board.comment.mapper;

import org.mapstruct.Mapper;

import shop.samgak.mini_board.comment.dto.CommentDTO;
import shop.samgak.mini_board.comment.entities.Comment;
import shop.samgak.mini_board.user.mapper.UserMapper;

/**
 * Comment 엔티티와 CommentDTO 간의 변환을 담당
 * MapStruct를 사용하여 엔티티와 DTO 사이의 자동 변환을 지원
 */
@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface CommentMapper {

    /**
     * Comment 엔티티를 CommentDTO로 변환
     * 
     * @param comment 변환할 Comment 엔티티 객체
     * @return 변환된 CommentDTO 객체
     */
    CommentDTO toDTO(Comment comment);

    /**
     * CommentDTO를 Comment 엔티티로 변환
     * 
     * @param commentDTO 변환할 CommentDTO 객체
     * @return 변환된 Comment 엔티티 객체
     */
    Comment fromDTO(CommentDTO commentDTO);
}
