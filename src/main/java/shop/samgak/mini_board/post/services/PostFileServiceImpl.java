package shop.samgak.mini_board.post.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.post.Repositories.PostFileRepository;
import shop.samgak.mini_board.post.dto.PostFileDTO;
import shop.samgak.mini_board.post.mapper.PostFileMapper; // MapStruct Mapper 추가

@Service
@RequiredArgsConstructor
public class PostFileServiceImpl implements PostFileService {
    final PostFileRepository postFileRepository;
    final PostFileMapper postFileMapper;

    @Override
    public List<PostFileDTO> getAll() {
        return postFileRepository.findAll().stream()
                .map(postFileMapper::postFileToPostFileDTO)
                .collect(Collectors.toList());
    }
}
