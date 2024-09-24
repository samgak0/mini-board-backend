package shop.samgak.mini_board.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import shop.samgak.mini_board.post.dto.PostFileDTO;
import shop.samgak.mini_board.post.entities.PostFile;

@Mapper(componentModel = "spring", uses = PostMapper.class)
public interface PostFileMapper {
    @Mapping(source = "original_name", target = "originalName")
    PostFileDTO postFileToPostFileDTO(PostFile postFile);

    @Mapping(source = "originalName", target = "original_name")
    PostFile postFileDTOToPostFile(PostFileDTO postFileDTO);
}
