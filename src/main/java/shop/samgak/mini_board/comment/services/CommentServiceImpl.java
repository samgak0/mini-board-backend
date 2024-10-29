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

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CommentDTO> get(Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream().map(commentMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public CommentDTO create(String content, Long postId, Long userId) {
        User user = entityManager.getReference(User.class, userId);
        Post post = entityManager.getReference(Post.class, postId);
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setPost(post);
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDTO(savedComment);
    }

    @Override
    public void update(Long commentId, String content, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("User not authorized to update this comment");
        }
        if (!comment.getContent().equals(content)) {
            comment.setContent(content);
            comment.setUpdatedAt(Instant.now());
        }
        commentRepository.save(comment);
    }

    @Override
    public void delete(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("User not authorized to delete this post");
        }
        commentRepository.delete(comment);
    }
}
