package shop.samgak.mini_board.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.dto.CommentFileDTO;
import shop.samgak.mini_board.entities.CommentFile;
import shop.samgak.mini_board.repositories.CommentFileRepository;

@Service
@RequiredArgsConstructor
public class CommentFileService {
    final CommentFileRepository commentFileRepository;

    public List<CommentFileDTO> getAll() {
        return commentFileRepository.findAll().stream().map(CommentFile::toDTO).collect(Collectors.toList());
    }
}
