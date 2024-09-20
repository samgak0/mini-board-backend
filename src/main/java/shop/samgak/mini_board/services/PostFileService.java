package shop.samgak.mini_board.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.dto.PostFileDTO;
import shop.samgak.mini_board.entities.PostFile;
import shop.samgak.mini_board.repositories.PostFileRepository;

@Service
@RequiredArgsConstructor
public class PostFileService {
    final PostFileRepository postFileRepository;

    public List<PostFileDTO> getAll() {
        return postFileRepository.findAll().stream().map(PostFile::toDTO).collect(Collectors.toList());
    }
}
