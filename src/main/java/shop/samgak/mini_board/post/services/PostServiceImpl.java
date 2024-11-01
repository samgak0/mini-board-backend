package shop.samgak.mini_board.post.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.exceptions.UnauthorizedActionException;
import shop.samgak.mini_board.post.dto.PostDTO;
import shop.samgak.mini_board.post.entities.Post;
import shop.samgak.mini_board.post.mapper.PostMapper;
import shop.samgak.mini_board.post.repositories.PostRepository;
import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.user.entities.User;

/**
 * 게시물 관련 기능을 구현하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private static final String SESSION_VIEWED_POSTS = "viewedPosts";
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PostDTO> getTop10() {
        // 삭제되지 않은 게시물 중 최신 순으로 상위 10개를 조회하여 DTO로 변환 후 반환
        return postRepository.findTop10ByDeletedAtIsNullOrderByCreatedAtDesc().stream()
                .map(postMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean increaseViewCount(Long postId, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Long> viewedPosts = (List<Long>) session.getAttribute(SESSION_VIEWED_POSTS);

        if (viewedPosts == null) {
            viewedPosts = new ArrayList<>();
        }

        if (!viewedPosts.contains(postId)) {
            increaseViewCount(postId);
            viewedPosts.add(postId);
            session.setAttribute(SESSION_VIEWED_POSTS, viewedPosts);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public PostDTO getPostById(Long postId) {
        return postMapper.toDTO(findPostOrThrow(postId));
    }

    @Override
    public Long create(String title, String content, UserDTO userDTO) {
        User user = entityManager.getReference(User.class, userDTO.getId());
        Post post = new Post(null, user, title, content, 0L, Instant.now(), Instant.now(), null, null);
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    @Override
    public void update(Long postId, String title, String content, UserDTO userDTO) {
        Post post = findPostOrThrow(postId);
        // 수정 권한이 있는 자신의 게시글인지 확인
        if (!post.getUser().getId().equals(userDTO.getId())) {
            throw new UnauthorizedActionException(
                    "User with ID " + userDTO.getId() + " not authorized to update post with ID " + postId);
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
        // 제목이나 내용이 수정된 경우에만 업데이트 시간을 갱신하고 저장
        if (isUpdated) {
            post.setUpdatedAt(Instant.now());
            postRepository.save(post);
        }
    }

    @Override
    public void delete(Long postId, UserDTO userDTO) {
        Post post = findPostOrThrow(postId);
        // 삭제 권한이 있는 자신의 게시글인지 확인
        if (!post.getUser().getId().equals(userDTO.getId())) {
            throw new UnauthorizedActionException(
                    "User with ID " + userDTO.getId() + " not authorized to update/delete post with ID " + postId);
        }
        post.setDeletedAt(Instant.now());
        postRepository.save(post);
    }

    @Override
    public boolean existsById(Long id) {
        // 해당 ID의 게시물이 존재하는지 확인
        return postRepository.existsById(id);
    }

    private void increaseViewCount(Long postId) {
        // 게시물을 찾고 조회수를 증가시킨 뒤 저장
        Post post = findPostOrThrow(postId);
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }

    private Post findPostOrThrow(Long postId) {
        // 삭제되지 않은 게시물을 찾습니다. 존재하지 않으면 예외를 발생
        return postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }
}
