package shop.samgak.mini_board.comment;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.post.PostLikeDTO;
import shop.samgak.mini_board.post.PostLikeService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/comment/like")
public class CommentLikeController {

    final PostLikeService postLikeService;

    @GetMapping
    public List<PostLikeDTO> getAllPostLike() {
        return postLikeService.getAll();
    }
}
