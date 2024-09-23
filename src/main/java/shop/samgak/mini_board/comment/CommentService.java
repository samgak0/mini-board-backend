<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/services/CommentService.java
package shop.samgak.mini_board.comment.services;
========
package shop.samgak.mini_board.comment;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentService.java

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import lombok.RequiredArgsConstructor;
<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/services/CommentService.java
import shop.samgak.mini_board.comment.dto.CommentDTO;
import shop.samgak.mini_board.comment.repository.CommentRepository;
========
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentService.java

@RequiredArgsConstructor
public class CommentService {
    final CommentRepository commentRepository;
    final ModelMapper modelMapper;

    public List<CommentDTO> getAll() {
        return commentRepository.findAll().stream()
                .map(comment -> modelMapper.map(comment, CommentDTO.class))
                .collect(Collectors.toList());
    }
}
