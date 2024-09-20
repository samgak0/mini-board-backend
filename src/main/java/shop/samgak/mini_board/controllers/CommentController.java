package shop.samgak.mini_board.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.dto.CommentDTO;
import shop.samgak.mini_board.services.CommentService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    final CommentService commentService;

    @GetMapping("/api/users")
    public List<CommentDTO> getAllComment() {
        return commentService.getAll();
    }
}
