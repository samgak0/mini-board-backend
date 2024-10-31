package shop.samgak.mini_board.post.mapper;

import org.mapstruct.Mapper;

import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.post.entities.Post;
import shop.samgak.mini_board.user.mapper.UserMapper;

/**
 * Post 엔티티와 DTO 간의 매핑을 처리하는 인터페이스
 */
@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface PostMapper {
    /**
     * Post 엔티티를 PostDTO로 변환하는 메서드
     * 
     * @param post 변환할 Post 엔티티
     * @return 변환된 PostDTO 객체
     */
    PostDTO toDTO(Post post);

    /**
     * PostDTO를 Post 엔티티로 변환하는 메서드
     * 
     * @param postDTO 변환할 PostDTO 객체
     * @return 변환된 Post 엔티티
     */
    Post fromDTO(PostDTO postDTO);
}
