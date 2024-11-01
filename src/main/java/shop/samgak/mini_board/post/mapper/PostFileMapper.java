package shop.samgak.mini_board.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import shop.samgak.mini_board.post.dto.PostFileDTO;
import shop.samgak.mini_board.post.entities.PostFile;

/**
 * PostFile 엔티티와 DTO 간의 매핑을 처리하는 인터페이스
 */
@Mapper(componentModel = "spring", uses = PostMapper.class)
public interface PostFileMapper {
    /**
     * PostFile 엔티티를 PostFileDTO로 변환하는 메서드
     * 
     * @param postFile 변환할 PostFile 엔티티
     * @return 변환된 PostFileDTO 객체
     */
    PostFileDTO toDTO(PostFile postFile);

    /**
     * PostFileDTO를 PostFile 엔티티로 변환하는 메서드
     * 
     * @param postFileDTO 변환할 PostFileDTO 객체
     * @return 변환된 PostFile 엔티티
     */
    @Mapping(target = "post", ignore = true)
    PostFile fromDTO(PostFileDTO postFileDTO);
}
