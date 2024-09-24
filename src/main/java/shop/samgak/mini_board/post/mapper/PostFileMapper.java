package shop.samgak.mini_board.post.mapper;

import org.mapstruct.Mapper;

import shop.samgak.mini_board.post.dto.PostFileDTO;
import shop.samgak.mini_board.post.entities.PostFile;

@Mapper(componentModel = "spring", uses = PostMapper.class)
public interface PostFileMapper {
    PostFileDTO postFileToPostFileDTO(PostFile postFile);

    PostFile postFileDTOToPostFile(PostFileDTO postFileDTO);
}