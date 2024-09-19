package shop.samgak.mini_board.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.entities.CommentLike;
import shop.samgak.mini_board.repositories.CommentLikeRepository;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    final CommentLikeRepository commentLikeRepository;

    public List<CommentLike> getAll() {
        return commentLikeRepository.findAll();
    }
}
