package shop.samgak.mini_board.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.dto.CommentDTO;
import shop.samgak.mini_board.repositories.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentService {
    final CommentRepository commentRepository;
    ModelMapper modelMapper;

    public List<CommentDTO> getAll() {
        return commentRepository.findAll().stream()
                .map(comment -> modelMapper.map(comment, CommentDTO.class))
                .collect(Collectors.toList());
    }
}
