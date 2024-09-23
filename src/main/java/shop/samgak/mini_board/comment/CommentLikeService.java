<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/services/CommentLikeService.java
package shop.samgak.mini_board.comment.services;
========
package shop.samgak.mini_board.comment;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentLikeService.java

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/services/CommentLikeService.java
import shop.samgak.mini_board.comment.dto.CommentLikeDTO;
import shop.samgak.mini_board.comment.repository.CommentLikeRepository;
========
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentLikeService.java

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    final CommentLikeRepository commentLikeRepository;
    final ModelMapper modelMapper;

    public List<CommentLikeDTO> getAll() {
        return commentLikeRepository.findAll().stream()
                .map(commentLike -> modelMapper.map(commentLike, CommentLikeDTO.class))
                .collect(Collectors.toList());
    }
}
