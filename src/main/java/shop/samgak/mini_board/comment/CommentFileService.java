<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/services/CommentFileService.java
package shop.samgak.mini_board.comment.services;
========
package shop.samgak.mini_board.comment;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentFileService.java

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import lombok.RequiredArgsConstructor;
<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/services/CommentFileService.java
import shop.samgak.mini_board.comment.dto.CommentFileDTO;
import shop.samgak.mini_board.comment.repository.CommentFileRepository;
========
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentFileService.java

@RequiredArgsConstructor
public class CommentFileService {
    final CommentFileRepository commentFileRepository;
    final ModelMapper modelMapper;

    public List<CommentFileDTO> getAll() {
        return commentFileRepository.findAll().stream()
                .map(commentFile -> modelMapper.map(commentFile,
                        CommentFileDTO.class))
                .collect(Collectors.toList());
    }
}
