package shop.samgak.mini_board.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.dto.CommentLikeDTO;
import shop.samgak.mini_board.entities.CommentLike;
import shop.samgak.mini_board.repositories.CommentLikeRepository;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    final CommentLikeRepository commentLikeRepository;

    public List<CommentLikeDTO> getAll() {
        return commentLikeRepository.findAll().stream().map(CommentLike::toDTO).collect(Collectors.toList());
    }
}
