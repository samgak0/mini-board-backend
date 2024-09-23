<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/services/PostLikeService.java
package shop.samgak.mini_board.post.services;
========
package shop.samgak.mini_board.post;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostLikeService.java

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/services/PostLikeService.java
import shop.samgak.mini_board.post.Repositories.PostLikeRepository;
import shop.samgak.mini_board.post.dto.PostLikeDTO;
========
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostLikeService.java

@Service
@RequiredArgsConstructor
public class PostLikeService {
    final PostLikeRepository postLikeRepository;
    final ModelMapper modelMapper;

    public List<PostLikeDTO> getAll() {
        return postLikeRepository.findAll().stream()
                .map(postLike -> modelMapper.map(postLike, PostLikeDTO.class))
                .collect(Collectors.toList());
    }
}
