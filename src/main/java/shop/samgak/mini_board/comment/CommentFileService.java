package shop.samgak.mini_board.comment;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import lombok.RequiredArgsConstructor;

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
