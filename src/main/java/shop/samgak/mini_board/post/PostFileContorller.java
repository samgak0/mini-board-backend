<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/controllers/PostFileContorller.java
package shop.samgak.mini_board.post.controllers;
========
package shop.samgak.mini_board.post;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostFileContorller.java

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/controllers/PostFileContorller.java
import shop.samgak.mini_board.post.dto.PostFileDTO;
import shop.samgak.mini_board.post.services.PostFileService;
========
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostFileContorller.java

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/post/files")
public class PostFileContorller {

    final PostFileService postFileService;

    @GetMapping
    public List<PostFileDTO> getAllPostFile() {
        return postFileService.getAll();
    }
}
