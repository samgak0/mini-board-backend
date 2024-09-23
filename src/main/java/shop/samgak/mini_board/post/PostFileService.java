<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/services/PostFileService.java
package shop.samgak.mini_board.post.services;
========
package shop.samgak.mini_board.post;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostFileService.java

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/services/PostFileService.java
import shop.samgak.mini_board.post.Repositories.PostFileRepository;
import shop.samgak.mini_board.post.dto.PostFileDTO;
========
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostFileService.java

@Service
@RequiredArgsConstructor
public class PostFileService {
    final PostFileRepository postFileRepository;
    final ModelMapper modelMapper;

    public List<PostFileDTO> getAll() {
        return postFileRepository.findAll().stream()
                .map(postFile -> modelMapper.map(postFile, PostFileDTO.class))
                .collect(Collectors.toList());
    }
}
