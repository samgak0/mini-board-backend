package shop.samgak.mini_board.post.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.post.dto.PostFileDTO;
import shop.samgak.mini_board.post.services.PostFileService;

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
