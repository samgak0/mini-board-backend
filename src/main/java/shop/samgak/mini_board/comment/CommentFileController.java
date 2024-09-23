<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/controllers/CommentFileController.java
package shop.samgak.mini_board.comment.controllers;
========
package shop.samgak.mini_board.comment;
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentFileController.java

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
<<<<<<<< HEAD:src/main/java/shop/samgak/mini_board/comment/controllers/CommentFileController.java
import shop.samgak.mini_board.comment.dto.CommentFileDTO;
import shop.samgak.mini_board.comment.services.CommentFileService;
========
>>>>>>>> ee801979c17f5c757c9d7a9c391714db90986425:src/main/java/shop/samgak/mini_board/comment/CommentFileController.java

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/comment/files")
public class CommentFileController {

    final CommentFileService commentFileService;

    @GetMapping
    public List<CommentFileDTO> getAllComment() {
        return commentFileService.getAll();
    }
}
