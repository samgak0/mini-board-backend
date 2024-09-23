package shop.samgak.mini_board.comment;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommentService {
    final CommentRepository commentRepository;
    final ModelMapper modelMapper;

    public List<CommentDTO> getAll() {
        return commentRepository.findAll().stream()
                .map(comment -> modelMapper.map(comment, CommentDTO.class))
                .collect(Collectors.toList());
    }
}
