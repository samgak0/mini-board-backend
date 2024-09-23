package shop.samgak.mini_board.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.dto.PostFileDTO;
import shop.samgak.mini_board.repositories.PostFileRepository;

@Service
@RequiredArgsConstructor
public class PostFileService {
    final PostFileRepository postFileRepository;
    ModelMapper modelMapper;

    public List<PostFileDTO> getAll() {
        return postFileRepository.findAll().stream()
                .map(postFile -> modelMapper.map(postFile, PostFileDTO.class))
                .collect(Collectors.toList());
    }
}
