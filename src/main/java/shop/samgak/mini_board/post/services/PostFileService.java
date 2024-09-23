package shop.samgak.mini_board.post.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.post.Repositories.PostFileRepository;
import shop.samgak.mini_board.post.dto.PostFileDTO;

@Service
@RequiredArgsConstructor
public class PostFileService {
    final PostFileRepository postFileRepository;
    final ModelMapper modelMapper;

    public List<PostFileDTO> getAll() {
        return postFileRepository.findAll().stream()
                .map(postFile -> modelMapper.map(postFile, PostFileDTO.class))
                .collect(Collectors.toList());
    }
}
