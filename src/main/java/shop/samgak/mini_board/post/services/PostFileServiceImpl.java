package shop.samgak.mini_board.post.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.post.dto.PostFileDTO;
import shop.samgak.mini_board.post.entities.Post;
import shop.samgak.mini_board.post.entities.PostFile;
import shop.samgak.mini_board.post.mapper.PostFileMapper;
import shop.samgak.mini_board.post.repositories.PostFileRepository;

@Service
@RequiredArgsConstructor
public class PostFileServiceImpl implements PostFileService {

    @PersistenceContext
    private EntityManager entityManager;

    final PostFileRepository postFileRepository;
    final PostFileMapper postFileMapper;

    @Override
    public List<PostFileDTO> getItemByPost(Long postId) {
        return postFileRepository.findByPostId(postId).stream().map(postFileMapper::toDTO).toList();
    }

    @Override
    public Optional<PostFileDTO> getItem(Long postFileId, Long postId) {
        return postFileRepository.findByIdAndPostId(postFileId, postId).map(postFileMapper::toDTO);
    }

    @Override
    public PostFileDTO writePostFileInfo(Long postId, String originalFileName, String filename, String contentType,
            long fileSize) {

        Post post = entityManager.getReference(Post.class, postId);
        PostFile postFile = new PostFile();
        postFile.setPost(post);
        postFile.setOriginalName(originalFileName);
        postFile.setFileName(filename);
        postFile.setContentType(contentType);
        postFile.setFileSize(fileSize);
        postFile.setHitCount(0L);
        postFile.setCreatedAt(Instant.now());

        return postFileMapper.toDTO(postFileRepository.save(postFile));
    }

}
