package shop.samgak.mini_board.post.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.config.UploadProperties;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.exceptions.ServerIOException;
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

    public final UploadProperties uploadProperties;

    @Override
    public List<PostFileDTO> getItemsByPost(Long postId) {
        List<PostFileDTO> postFiles = postFileRepository.findByPostId(postId).stream()
                .map(postFileMapper::toDTO)
                .toList();
        return postFiles;
    }

    @Override
    public PostFileDTO getItem(Long postFileId, Long postId) {
        return postFileRepository.findByIdAndPostId(postFileId, postId)
                .map(postFileMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PostFile not found with Post ID: [" + postId + "] and PostFile ID: [" + postFileId + "]"));
    }

    @Override
    public boolean increaseViewCount(Long postFileId, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Long> viewedPosts = (List<Long>) session.getAttribute(SESSION_DOWNLOADED_FILE);

        if (viewedPosts == null) {
            viewedPosts = new ArrayList<>();
        }

        if (!viewedPosts.contains(postFileId)) {
            increaseViewCount(postFileId);
            viewedPosts.add(postFileId);
            session.setAttribute(SESSION_DOWNLOADED_FILE, viewedPosts);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void increaseViewCount(Long postFileId) {
        PostFile postFile = findPostFileOrThrow(postFileId);
        postFile.setViewCount(postFile.getViewCount() + 1);
        postFileRepository.save(postFile);
    }

    @Override
    public Path writePostFile(MultipartFile file, Path generatedPath, Long postId) {
        try {
            Path uploadPath = Paths.get(uploadProperties.getUploadDir());
            if (!Files.exists(uploadPath)) {
                log.warn("Upload directory does not exist. Creating directory at: [{}]", uploadPath);
                Files.createDirectories(uploadPath);
            }
            file.transferTo(generatedPath.toFile());
            return generatedPath;
        } catch (IOException e) {
            String errorMessage = "Failed to write post file for post ID: " + postId;
            log.error(errorMessage, e);
            throw new ServerIOException(errorMessage);
        }
    }

    @Override
    public PostFileDTO writePostFileInfo(Long postId, String originalFileName, String filename, String contentType,
            long fileSize, UserDTO userDTO) {
        Post post = findPostOrThrow(postId);
        if (!post.getUser().getId().equals(userDTO.getId())) {
            log.warn("User ID: {} is not authorized to upload a file for post ID: [{}]", userDTO.getId(), postId);
            throw new UnauthorizedActionException(
                    String.format("User with ID [%d] not authorized to upload post file for post ID [%d]",
                            userDTO.getId(), postId));
        }
        PostFile postFile = new PostFile(null, post, originalFileName, filename, contentType, fileSize, 0L,
                Instant.now());

        PostFile savedFile = postFileRepository.save(postFile);
        return postFileMapper.toDTO(savedFile);
    }

    @Override
    public boolean deleteFile(String fileName) throws IOException {
        log.info("Deleting file: {}", fileName);
        Path uploadPath = Paths.get(uploadProperties.getUploadDir());
        Path filePath = uploadPath.resolve(fileName);
        return Files.deleteIfExists(filePath);
    }

    @Override
    public PostFileDTO deleteFileInfo(Long postFileId, UserDTO userDTO) {
        PostFile postFile = findPostFileOrThrow(postFileId);
        if (!postFile.getPost().getUser().getId().equals(userDTO.getId())) {
            log.warn("User ID: {} is not authorized to delete file info with ID: [{}]", userDTO.getId(), postFileId);
            throw new UnauthorizedActionException(
                    String.format("User with ID [%d] not authorized to delete info file with ID [%d]", userDTO.getId(),
                            postFileId));
        }
        postFileRepository.delete(postFile);
        return postFileMapper.toDTO(postFile);
    }

    private Post findPostOrThrow(Long postId) {
        log.debug("Fetching post with ID: {}", postId);
        return postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }

    private PostFile findPostFileOrThrow(Long postFileId) {
        log.debug("Fetching post file with ID: {}", postFileId);
        return postFileRepository.findById(postFileId)
                .orElseThrow(() -> new ResourceNotFoundException("Post File not found with id: " + postFileId));
    }

    @Override
    public Path generateUniqueFilePath() {
        Path uploadPath = Paths.get(uploadProperties.getUploadDir());
        int maxRetry = uploadProperties.getMaxRetry();

        for (int count = 1; count <= maxRetry; count++) {
            String filename = UUID.randomUUID().toString().replace("-", "");
            Path filePath = uploadPath.resolve(filename);

            if (!Files.exists(filePath)) {
                // 파일 쓰기 권한 여부를 검사해서 권한이 없을 때에도 재생성을 시도한다.
                if (!Files.isWritable(filePath)) {
                    log.error("Generated file name [{}] is not writable. - trying to generate new file name [{}/{}]",
                            filePath.getFileName(),
                            count, maxRetry);
                    continue;
                }
                return filePath;
            }

            log.error("Generated file name [{}] already exists. - trying to generate new file name [{}/{}]",
                    filePath.getFileName(),
                    count, maxRetry);
        }

        String errorMessage = "Unable to generate a unique file name after " + maxRetry + " retries";
        log.error(errorMessage);
        throw new ServerIOException(errorMessage);
    }
}
