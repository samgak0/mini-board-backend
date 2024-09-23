package shop.samgak.mini_board.comment;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
