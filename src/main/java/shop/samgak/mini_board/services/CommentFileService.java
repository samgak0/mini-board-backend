package shop.samgak.mini_board.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.dto.CommentFileDTO;
import shop.samgak.mini_board.repositories.CommentFileRepository;

@Service
@RequiredArgsConstructor
public class CommentFileService {
    final CommentFileRepository commentFileRepository;
    final ModelMapper modelMapper;

    public List<CommentFileDTO> getAll() {
        return commentFileRepository.findAll().stream()
                .map(commentFile -> modelMapper.map(commentFile,
                        CommentFileDTO.class))
                .collect(Collectors.toList());
    }
}
