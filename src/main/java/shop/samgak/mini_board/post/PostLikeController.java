<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/controllers/PostLikeController.java
package shop.samgak.mini_board.post.controllers;
========
package shop.samgak.mini_board.post;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostLikeController.java

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/post/controllers/PostLikeController.java
import shop.samgak.mini_board.post.dto.PostLikeDTO;
import shop.samgak.mini_board.post.services.PostLikeService;
========
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/post/PostLikeController.java

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/post/like")
public class PostLikeController {

    final PostLikeService postLikeService;

    @GetMapping
    public List<PostLikeDTO> getAllPostLike() {
        return postLikeService.getAll();
    }
}
