package shop.samgak.mini_board.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.entities.CommentFile;
import shop.samgak.mini_board.repositories.CommentFileRepository;

@Service
@RequiredArgsConstructor
public class CommentFileService {
    final CommentFileRepository commentFileRepository;

    public List<CommentFile> getAll() {
        return commentFileRepository.findAll();
    }
}
