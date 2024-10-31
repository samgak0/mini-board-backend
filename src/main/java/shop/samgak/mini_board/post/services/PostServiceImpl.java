package shop.samgak.mini_board.post.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 최신 게시물 상위 10개를 가져오는 메서드
     * 
     * @return 최신 게시물 상위 10개의 목록
     */
    @Override
    public List<PostDTO> getTop10() {
        log.debug("최신 게시물 상위 10개 조회");
        // 삭제되지 않은 게시물 중 최신 순으로 상위 10개를 조회하여 DTO로 변환 후 반환
        return postRepository.findTop10ByIsDeletedFalseOrderByCreatedAtDesc().stream()
                .map(postMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 특정 게시물의 조회수를 증가시키는 메서드 (세션을 사용하여 중복 조회 방지)
     * 
     * @param postId  게시물 ID
     * @param session 현재 세션 객체
     */
    @Override
    public void increaseViewCount(Long postId, HttpSession session) {
        log.debug("게시물 ID {}의 조회수 증가", postId);
        @SuppressWarnings("unchecked")
        // 세션에서 이미 조회한 게시물 목록을 가져옵니다.
        List<Long> viewedPosts = (List<Long>) session.getAttribute(SESSION_VIEWED_POSTS);

        if (viewedPosts == null) {
            // 조회한 게시물 목록이 없으면 새로운 리스트를 생성
            viewedPosts = new ArrayList<>();
        }

        // 현재 게시물이 조회된 적이 없는 경우에만 조회수를 증가
        if (!viewedPosts.contains(postId)) {
            increaseViewCount(postId);
            viewedPosts.add(postId);
        }

        // 업데이트된 조회한 게시물 목록을 세션에 저장
        session.setAttribute(SESSION_VIEWED_POSTS, viewedPosts);
    }

    /**
     * 특정 게시물의 조회수를 증가시키는 메서드
     * 
     * @param postId 게시물 ID
     */
    @Override
    public void increaseViewCount(Long postId) {
        log.debug("게시물 ID {}의 조회수 증가", postId);
        // 게시물을 찾고 조회수를 증가시킨 뒤 저장
        Post post = findPostOrThrow(postId);
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }

    /**
     * 특정 ID의 게시물을 가져오는 메서드
     * 
     * @param postId 게시물 ID
     * @return 해당 게시물의 정보
     */
    @Override
    public PostDTO getPostById(Long postId) {
        log.debug("게시물 ID {}의 정보 조회", postId);
        // 게시물을 찾아 DTO로 변환하여 반환
        return postMapper.toDTO(findPostOrThrow(postId));
    }

    /**
     * 새로운 게시물을 생성하는 메서드
     * 
     * @param title   게시물 제목
     * @param content 게시물 내용
     * @param userDTO 작성자 정보
     * @return 생성된 게시물의 ID
     */
    @Transactional
    @Override
    public Long create(String title, String content, UserDTO userDTO) {
        log.info("사용자 ID {}가 새로운 게시물 생성", userDTO.getId());
        // 사용자 정보를 가져와 게시물 엔티티를 생성하고 저장
        User user = entityManager.getReference(User.class, userDTO.getId());
        Post post = new Post(null, user, title, content, 0L, Instant.now(), Instant.now(), false, null);
        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    /**
     * 특정 게시물을 수정하는 메서드
     * 
     * @param postId  수정할 게시물 ID
     * @param title   수정할 제목
     * @param content 수정할 내용
     * @param userDTO 수정 요청 사용자 정보
     */
    @Transactional
    @Override
    public void update(Long postId, String title, String content, UserDTO userDTO) {
        log.info("사용자 ID {}가 게시물 ID {} 수정", userDTO.getId(), postId);
        // 게시물을 찾고 수정 권한을 확인한 뒤 내용을 업데이트
        Post post = findPostOrThrow(postId);
        if (!post.getUser().getId().equals(userDTO.getId())) {
            throw new UnauthorizedActionException(
                    "User with ID " + userDTO.getId() + " not authorized to update/delete post with ID " + postId);
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

    /**
     * 특정 게시물을 삭제하는 메서드
     * 
     * @param postId  삭제할 게시물 ID
     * @param userDTO 삭제 요청 사용자 정보
     */
    @Transactional
    @Override
    public void delete(Long postId, UserDTO userDTO) {
        log.info("사용자 ID {}가 게시물 ID {} 삭제", userDTO.getId(), postId);
        // 게시물을 찾고 삭제 권한을 확인한 뒤 삭제 플래그를 설정
        Post post = findPostOrThrow(postId);
        if (!post.getUser().getId().equals(userDTO.getId())) {
            throw new UnauthorizedActionException(
                    "User with ID " + userDTO.getId() + " not authorized to update/delete post with ID " + postId);
        }
        post.setIsDeleted(true);
        postRepository.save(post);
    }

    /**
     * 특정 ID의 게시물이 존재하는지 확인하는 메서드
     * 
     * @param id 게시물 ID
     * @return 게시물 존재 여부
     */
    @Override
    public boolean existsById(Long id) {
        log.debug("게시물 ID {} 존재 여부 확인", id);
        // 해당 ID의 게시물이 존재하는지 확인
        return postRepository.existsById(id);
    }

    /**
     * 특정 게시물을 찾는 메서드 (존재하지 않으면 예외 발생)
     * 
     * @param postId 게시물 ID
     * @return 게시물 엔티티
     */
    private Post findPostOrThrow(Long postId) {
        log.debug("게시물 ID {} 찾기", postId);
        // 삭제되지 않은 게시물을 찾습니다. 존재하지 않으면 예외를 발생
        return postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }
}
