package shop.samgak.mini_board.post.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.exceptions.UnauthorizedActionException;
import shop.samgak.mini_board.post.dto.PostFileDTO;
import shop.samgak.mini_board.post.entities.Post;
import shop.samgak.mini_board.post.entities.PostFile;
import shop.samgak.mini_board.post.mapper.PostFileMapper;
import shop.samgak.mini_board.post.repositories.PostFileRepository;
import shop.samgak.mini_board.post.repositories.PostRepository;
import shop.samgak.mini_board.user.dto.UserDTO;

/**
 * 게시물 파일 관련 기능을 구현하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostFileServiceImpl implements PostFileService {
    private static final String SESSION_DOWNLOADED_FILE = "downloadedPostFile";

    private final PostFileRepository postFileRepository;
    private final PostRepository postRepository;
    private final PostFileMapper postFileMapper;

    /**
     * 특정 게시물에 첨부된 파일 목록을 가져오는 메서드
     * 
     * @param postId 게시물 ID
     * @return 해당 게시물에 첨부된 파일 목록
     */
    @Override
    public List<PostFileDTO> getItemByPost(Long postId) {
        log.info("Fetching files for postId: [{}]", postId);
        List<PostFileDTO> postFiles = postFileRepository.findByPostIdAndIsDeletedFalse(postId).stream()
                .map(postFileMapper::toDTO)
                .toList();
        log.info("Found [{}] files for postId: [{}]", postFiles.size(), postId);
        return postFiles;
    }

    /**
     * 특정 게시물에 속한 특정 파일을 가져오는 메서드
     * 
     * @param postFileId 파일 ID
     * @param postId     게시물 ID
     * @return 해당 파일의 정보
     */
    @Override
    public PostFileDTO getItem(Long postFileId, Long postId) {
        log.info("Fetching file with postFileId: [{}] for postId: [{}]", postFileId, postId);
        return postFileRepository.findByIdAndPostIdAndIsDeletedFalse(postFileId, postId)
                .map(postFileMapper::toDTO)
                .orElseThrow(() -> {
                    log.warn("Post file not found with postId: [{}] and postFileId: [{}]", postId, postFileId);
                    return new ResourceNotFoundException(
                            "Post file not found with postId: " + postId + " and postFileId: " + postFileId);
                });
    }

    /**
     * 특정 게시물 파일의 조회수를 증가시키는 메서드 (세션을 사용하여 중복 조회 방지)
     * 
     * @param postFileId 파일 ID
     * @param session    현재 세션 객체
     */
    @Override
    public void increaseViewCount(Long postFileId, HttpSession session) {
        log.debug("Attempting to increase view count for postFileId: [{}]", postFileId);
        @SuppressWarnings("unchecked")
        List<Long> viewedPosts = (List<Long>) session.getAttribute(SESSION_DOWNLOADED_FILE);

        if (viewedPosts == null) {
            viewedPosts = new ArrayList<>();
        }

        if (!viewedPosts.contains(postFileId)) {
            log.debug("Increasing view count for postFileId: [{}]", postFileId);
            increaseViewCount(postFileId);
            viewedPosts.add(postFileId);
        } else {
            log.debug("View count for postFileId: [{}] not increased due to existing session record", postFileId);
        }

        session.setAttribute(SESSION_DOWNLOADED_FILE, viewedPosts);
    }

    /**
     * 특정 게시물 파일의 조회수를 증가시키는 메서드
     * 
     * @param postFileId 파일 ID
     */
    @Transactional
    @Override
    public void increaseViewCount(Long postFileId) {
        log.debug("Directly increasing view count for postFileId: [{}]", postFileId);
        PostFile postFile = findPostFileOrThrow(postFileId);
        postFile.setViewCount(postFile.getViewCount() + 1);
        postFileRepository.save(postFile);
        log.info("View count successfully increased for postFileId: [{}]", postFileId);
    }

    /**
     * 게시물 파일 정보를 데이터베이스에 저장하는 메서드
     * 
     * @param postId           게시물 ID
     * @param originalFileName 원본 파일 이름
     * @param filename         서버에 저장된 파일 이름
     * @param contentType      파일의 MIME 타입
     * @param fileSize         파일 크기
     * @param userDTO          현재 사용자 정보
     * @return 저장된 파일의 정보
     */
    @Transactional
    @Override
    public PostFileDTO writePostFileInfo(Long postId, String originalFileName, String filename, String contentType,
            long fileSize, UserDTO userDTO) {
        log.info("Attempting to save file info for postId: [{}], filename: [{}]", postId, filename);
        Post post = findPostOrThrow(postId);
        if (!post.getUser().getId().equals(userDTO.getId())) {
            log.warn("Unauthorized attempt by userId: [{}] to upload file for postId: [{}]", userDTO.getId(), postId);
            throw new UnauthorizedActionException(
                    "User with ID " + userDTO.getId() + " not authorized to upload post file for post ID " + postId);
        }
        PostFile postFile = new PostFile(null, post, originalFileName, filename, contentType, fileSize, 0L, false,
                Instant.now());

        PostFile savedFile = postFileRepository.save(postFile);
        log.info("File info successfully saved for postFileId: [{}]", savedFile.getId());
        return postFileMapper.toDTO(savedFile);
    }

    /**
     * 특정 파일을 삭제하는 메서드
     * 
     * @param postFileId 파일 ID
     * @param userDTO    현재 사용자 정보
     */
    @Transactional
    @Override
    public void deleteFile(Long postFileId, UserDTO userDTO) {
        log.info("Attempting to delete file with postFileId: [{}] by userId: [{}]", postFileId, userDTO.getId());
        PostFile postFile = findPostFileOrThrow(postFileId);
        if (!postFile.getPost().getUser().getId().equals(userDTO.getId())) {
            log.warn("Unauthorized attempt by userId: [{}] to delete file with postFileId: [{}]", userDTO.getId(),
                    postFileId);
            throw new UnauthorizedActionException(
                    "User with ID " + userDTO.getId() + " not authorized to delete file with ID " + postFileId);
        }
        postFile.setIsDeleted(true);
        postFileRepository.save(postFile);
        log.info("File successfully marked as deleted - postFileId: [{}]", postFileId);
    }

    /**
     * 특정 게시물을 찾는 메서드 (존재하지 않으면 예외 발생)
     * 
     * @param postId 게시물 ID
     * @return 게시물 엔티티
     */
    private Post findPostOrThrow(Long postId) {
        log.debug("Attempting to find post with postId: [{}]", postId);
        return postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> {
                    log.warn("Post not found with postId: [{}]", postId);
                    return new ResourceNotFoundException("Post not found with id: " + postId);
                });
    }

    /**
     * 특정 파일을 찾는 메서드 (존재하지 않으면 예외 발생)
     * 
     * @param postFileId 파일 ID
     * @return 파일 엔티티
     */
    private PostFile findPostFileOrThrow(Long postFileId) {
        log.debug("Attempting to find post file with postFileId: [{}]", postFileId);
        return postFileRepository.findByIdAndIsDeletedFalse(postFileId)
                .orElseThrow(() -> {
                    log.warn("Post file not found with postFileId: [{}]", postFileId);
                    return new ResourceNotFoundException("Post File not found with id: " + postFileId);
                });
    }
}
