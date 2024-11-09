package shop.samgak.mini_board.comment.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.comment.dto.CommentDTO;
import shop.samgak.mini_board.comment.entities.Comment;
import shop.samgak.mini_board.comment.mapper.CommentMapper;
import shop.samgak.mini_board.comment.repository.CommentRepository;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.exceptions.UnauthorizedActionException;
import shop.samgak.mini_board.post.entities.Post;
import shop.samgak.mini_board.post.repositories.PostRepository;
import shop.samgak.mini_board.user.entities.User;
import shop.samgak.mini_board.user.repositories.UserRepository;

/**
 * 댓글 관련 기능을 제공하는 서비스 클래스 구현체
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Id : [" + userId + "]"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with Id : [" + postId + "]"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setPost(post);
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        Hibernate.initialize(comment.getUser());

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDTO(savedComment);
    }

    /**
     * 특정 댓글을 수정합니다.
     * 
     * @param commentId 수정할 댓글의 ID
     * @param postId    게시글 ID
     * @param content   수정할 내용
     * @param userId    사용자 ID
     * @throws UnauthorizedActionException 사용자가 댓글을 수정할 권한이 없는 경우
     */
    @Override
    public void update(Long commentId, Long postId, String content, Long userId) {
        Comment comment = findCommentOrThrow(commentId, postId);

        if (!comment.getUser().getId().equals(userId)) {
            log.warn("The user with ID [{}] is not authorized to update the comment with ID [{}] on post with ID [{}]",
                    userId, commentId, postId);
            throw new UnauthorizedActionException("User not authorized to update this comment");
        }

        if (!comment.getContent().equals(content)) {
            comment.setContent(content);
            comment.setUpdatedAt(Instant.now());
            commentRepository.save(comment);
            log.info("Successfully updated comment with id: [{}]", commentId);
        } else {
            log.warn("No content change detected for commentId: [{}]", commentId);
        }
    }

    /**
     * 특정 댓글을 삭제합니다.
     * 
     * @param commentId 삭제할 댓글의 ID
     * @param postId    게시글 ID
     * @param userId    사용자 ID
     * @throws UnauthorizedActionException 사용자가 댓글을 삭제할 권한이 없는 경우
     */
    @Override
    public void delete(Long commentId, Long postId, Long userId) {
        Comment comment = findCommentOrThrow(commentId, postId);

        if (!comment.getUser().getId().equals(userId)) {
            log.warn("The user with ID [{}] is not authorized to delete the comment with ID [{}] on post with ID [{}]",
                    userId, commentId, postId);
            throw new UnauthorizedActionException("User not authorized to delete this comment");
        }

        commentRepository.delete(comment);
        log.info("Successfully deleted comment with id: [{}]", commentId);
    }

    /**
     * 특정 댓글을 조회하여 수정 또는 삭제를 위해 반환합니다.
     *
     * @param commentId 댓글 ID
     * @param postId    게시글 ID
     * @return Comment 객체
     * @throws ResourceNotFoundException 댓글이 존재하지 않는 경우
     */
    private Comment findCommentOrThrow(Long commentId, Long postId) {
        return commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException(
                            String.format("Comment not found with commentId: [%d], postId: [%d]",
                                    commentId, postId));
                });
    }

}
