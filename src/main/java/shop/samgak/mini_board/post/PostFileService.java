package shop.samgak.mini_board.post;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

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
