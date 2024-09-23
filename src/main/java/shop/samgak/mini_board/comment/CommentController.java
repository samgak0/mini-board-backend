<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/controllers/CommentController.java
package shop.samgak.mini_board.comment.controllers;
========
package shop.samgak.mini_board.comment;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentController.java

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/controllers/CommentController.java
import shop.samgak.mini_board.comment.dto.CommentDTO;
import shop.samgak.mini_board.comment.services.CommentService;
========
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentController.java

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/comments")
public class CommentController {

    final CommentService commentService;

    @GetMapping
    public List<CommentDTO> getAllComment() {
        return commentService.getAll();
    }
}
