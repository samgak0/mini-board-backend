package shop.samgak.mini_board.post.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.post.entities.Post;
import shop.samgak.mini_board.post.mapper.PostMapper;
import shop.samgak.mini_board.post.repositories.PostRepository;
import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.user.entities.User;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PostDTO> getTop10() {
        return postRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(postMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PostDTO getPostById(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
    }

    @Override
    public Long create(String title, String content, UserDTO userDTO) {
        User user = entityManager.getReference(User.class, userDTO.getId());
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setUser(user);
        post.setUpdatedAt(Instant.now());
        post.setCreatedAt(Instant.now());
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    @Override
    public void update(Long id, String title, String content, UserDTO userDTO) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        if (!post.getUser().getId().equals(userDTO.getId())) {
            throw new AccessDeniedException("User not authorized to update this post");
        }
        boolean isUpdated = false;
        if (!post.getTitle().equals(title)) {
            post.setTitle(title);
            isUpdated = true;
        }
        if (!post.getContent().equals(content)) {
            post.setContent(content);
            isUpdated = true;
        }
        if (isUpdated) {
            post.setUpdatedAt(Instant.now());
        }
        postRepository.save(post);
    }

    @Override
    public void delete(Long id, UserDTO userDTO) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        if (!post.getUser().getId().equals(userDTO.getId())) {
            throw new AccessDeniedException("User not authorized to delete this post");
        }
        postRepository.delete(post);
    }

    @Override
    public boolean existsById(Long id) {
        return postRepository.existsById(id);
    }
}
