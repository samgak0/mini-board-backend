package shop.samgak.mini_board.comment.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.comment.dto.CommentFileDTO;
import shop.samgak.mini_board.comment.mapper.CommentFileMapper;
import shop.samgak.mini_board.comment.repository.CommentFileRepository;

@Service
@RequiredArgsConstructor
public class CommentFileServiceImpl implements CommentFileService {
    final CommentFileRepository commentFileRepository;
    final CommentFileMapper commentFileMapper;

    @Override
    public List<CommentFileDTO> getAll() {
        return commentFileRepository.findAll().stream()
                .map(commentFileMapper::toDTO)
                .collect(Collectors.toList());
    }
}
