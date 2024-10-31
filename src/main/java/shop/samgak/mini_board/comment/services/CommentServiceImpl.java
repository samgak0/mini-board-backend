package shop.samgak.mini_board.comment.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import shop.samgak.mini_board.comment.dto.CommentDTO;
import shop.samgak.mini_board.comment.entities.Comment;
import shop.samgak.mini_board.comment.mapper.CommentMapper;
import shop.samgak.mini_board.comment.repository.CommentRepository;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.exceptions.UnauthorizedActionException;
import shop.samgak.mini_board.post.entities.Post;
import shop.samgak.mini_board.user.entities.User;

/**
 * 댓글 관련 기능을 제공하는 서비스 클래스 구현체
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 특정 게시물의 모든 댓글을 조회합니다.
     * 
     * @param postId 조회할 게시물의 ID
     * @return 해당 게시물에 대한 댓글 목록
     */
    @Override
    public List<CommentDTO> get(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream().map(commentMapper::toDTO).collect(Collectors.toList());
    }

    /**
     * 새로운 댓글을 생성합니다.
     * 
     * @param content 댓글 내용
     * @param postId  댓글이 속할 게시물의 ID
     * @param userId  댓글을 작성하는 사용자의 ID
     * @return 생성된 댓글의 DTO
     */
    @Override
    public CommentDTO create(String content, Long postId, Long userId) {
        // 사용자와 게시물 정보를 엔티티로 가져옴
        User user = entityManager.getReference(User.class, userId);
        Post post = entityManager.getReference(Post.class, postId);

        // 새로운 댓글 객체를 생성하고 설정함
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setPost(post);
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());

        // 댓글을 저장하고 DTO로 변환하여 반환
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDTO(savedComment);
    }

    /**
     * 특정 댓글을 수정합니다.
     * 
     * @param commentId 수정할 댓글의 ID
     * @param content   수정할 댓글 내용
     * @param userId    댓글을 수정하는 사용자의 ID
     * @throws UnauthorizedActionException 사용자가 댓글을 수정할 권한이 없는 경우 발생
     */
    @Override
    public void update(Long commentId, String content, Long userId) {
        // 댓글을 ID로 조회하고, 없으면 예외 발생
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        // 댓글 작성자와 요청 사용자가 일치하지 않으면 권한 없음 예외 발생
        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("User not authorized to update this comment");
        }

        // 댓글 내용이 수정된 경우에만 업데이트 수행
        if (!comment.getContent().equals(content)) {
            comment.setContent(content);
            comment.setUpdatedAt(Instant.now());
        }

        // 수정된 댓글을 저장함
        commentRepository.save(comment);
    }

    /**
     * 특정 댓글을 삭제합니다.
     * 
     * @param commentId 삭제할 댓글의 ID
     * @param userId    댓글을 삭제하는 사용자의 ID
     * @throws UnauthorizedActionException 사용자가 댓글을 삭제할 권한이 없는 경우 발생
     */
    @Override
    public void delete(Long commentId, Long userId) {
        // 댓글을 ID로 조회하고, 없으면 예외 발생.
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        // 댓글 작성자와 요청 사용자가 일치하지 않으면 권한 없음 예외 발생
        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("User not authorized to delete this post");
        }

        // 댓글을 삭제함
        commentRepository.delete(comment);
    }
}
