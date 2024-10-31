package shop.samgak.mini_board.post.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Value("${app.uploadDir}")
    private String uploadDir;

    /**
     * 특정 게시물에 첨부된 파일 목록을 가져오는 메서드
     * 
     * @param postId 게시물 ID
     * @return 해당 게시물에 첨부된 파일 목록
     */
    @Override
    public List<PostFileDTO> getItemByPost(Long postId) {
        log.debug("게시물 ID [{}]에 대한 파일 목록을 조회", postId);
        // 게시물 ID에 해당하는 파일 목록을 조회하고 DTO로 변환하여 반환
        List<PostFileDTO> postFiles = postFileRepository.findByPostIdAndIsDeletedFalse(postId).stream()
                .map(postFileMapper::toDTO)
                .toList();
        log.debug("게시물 ID [{}]에 대한 [{}]개의 파일을 찾았습니다.", postId, postFiles.size());
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
        log.debug("게시물 ID [{}]와 파일 ID [{}]에 대한 파일을 조회", postId, postFileId);
        // 특정 게시물 ID와 파일 ID로 파일 정보를 조회하여 DTO로 변환하여 반환
        return postFileRepository.findByIdAndPostIdAndIsDeletedFalse(postFileId, postId)
                .map(postFileMapper::toDTO)
                .orElseThrow(() -> {
                    // 파일이 없을 경우 예외를 발생시킴
                    log.warn("게시물 ID [{}]와 파일 ID [{}]에 대한 파일을 찾을 수 없습니다.", postId, postFileId);
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
        log.debug("파일 ID [{}]의 조회수를 증가시키려고 합니다.", postFileId);
        // 세션에서 이미 조회한 파일 목록을 가져옴
        @SuppressWarnings("unchecked")
        List<Long> viewedPosts = (List<Long>) session.getAttribute(SESSION_DOWNLOADED_FILE);

        if (viewedPosts == null) {
            log.debug("이전에 조회한 파일 목록이 없으므로 새 목록을 생성합니다.");
            // 조회한 파일 목록이 없으면 새로 생성
            viewedPosts = new ArrayList<>();
        }

        if (!viewedPosts.contains(postFileId)) {
            log.debug("파일 ID [{}]가 세션에 조회되지 않았으므로 조회수를 증가시킵니다.", postFileId);
            // 파일을 이전에 조회하지 않았으면 조회수를 증가시키고 목록에 추가
            increaseViewCount(postFileId);
            viewedPosts.add(postFileId);
        } else {
            log.debug("파일 ID [{}]는 이미 세션에 조회된 상태입니다. 조회수를 증가시키지 않습니다.", postFileId);
        }

        // 세션에 업데이트된 조회한 파일 목록을 저장
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
        log.debug("파일 ID [{}]의 조회수를 증가시키려 합니다.", postFileId);
        // 파일 ID로 파일을 조회하고 조회수를 1 증가시킨 후 저장
        PostFile postFile = findPostFileOrThrow(postFileId);
        postFile.setViewCount(postFile.getViewCount() + 1);
        postFileRepository.save(postFile);
        log.info("파일 ID [{}]의 조회수가 성공적으로 증가되었습니다.", postFileId);
    }

    @Override
    public Path writePostFile(MultipartFile file, Path genderatedPath, Long postId) {
        try {
            // 업로드 디렉토리 확인 및 생성
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                log.debug("업로드 디렉토리가 존재하지 않습니다. - 디렉토리 생성 중: [{}]", uploadDir);
                Files.createDirectories(uploadPath);
            }
            // 파일을 디스크에 저장
            file.transferTo(genderatedPath.toFile());
            log.info("파일 저장에 성공했습니다. - 경로: [{}]", genderatedPath);
            return genderatedPath;
        } catch (IOException e) {
            log.error("파일 업로드에 실패했습니다. - postId: [{}], 오류: [{}]", postId, e.getMessage());
            throw new ServerIOException();
        }
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
        log.debug("게시물 ID [{}]에 대한 파일 정보를 저장하려고 합니다. 파일명: [{}]", postId, filename);
        // 게시물 ID로 게시물을 찾고 사용자가 해당 게시물의 작성자인지 확인
        Post post = findPostOrThrow(postId);
        if (!post.getUser().getId().equals(userDTO.getId())) {
            log.warn("사용자 ID [{}]가 게시물 ID [{}]에 파일을 업로드하려고 했으나 권한이 없습니다.", userDTO.getId(), postId);
            // 작성자가 아니면 권한 없음 예외 발생
            throw new UnauthorizedActionException(
                    "User with ID " + userDTO.getId() + " not authorized to upload post file for post ID " + postId);
        }
        // 파일 정보를 생성하여 데이터베이스에 저장
        PostFile postFile = new PostFile(null, post, originalFileName, filename, contentType, fileSize, 0L, false,
                Instant.now());

        PostFile savedFile = postFileRepository.save(postFile);
        log.info("파일 정보가 성공적으로 저장되었습니다. 신규 파일 ID: [{}]", savedFile.getId());
        return postFileMapper.toDTO(savedFile);
    }

    @Override
    public boolean deleteFile(String fileName) {
        log.debug("파일 {} 을 물리적으로 삭제 시도합니다.", fileName);

        Path uploadPath = Paths.get(uploadDir);
        Path filePath = uploadPath.resolve(fileName);
        try {
            boolean isDeleted = Files.deleteIfExists(filePath);
            if (isDeleted) {
                log.debug("파일 {} 을 물리적으로 삭제 성공했습니다.", fileName);
            } else {
                log.warn("파일 {} 을 물리적으로 삭제 시도 하였으나 파일이 존재하지 않습니다.", fileName);
            }
            return isDeleted;
        } catch (IOException e) {
            throw new ServerIOException();
        }
    }

    /**
     * 특정 파일을 삭제하는 메서드
     * 
     * @param postFileId 파일 ID
     * @param userDTO    현재 사용자 정보
     */
    @Transactional
    @Override
    public PostFileDTO deleteFileInfo(Long postFileId, UserDTO userDTO) {
        log.info("사용자 ID [{}]가 파일 ID [{}] 정보를 삭제 시도합니다.", userDTO.getId(), postFileId);
        // 파일 ID로 파일을 찾고 사용자가 해당 파일의 게시물 작성자인지 확인
        PostFile postFile = findPostFileOrThrow(postFileId);
        if (!postFile.getPost().getUser().getId().equals(userDTO.getId())) {
            log.warn("사용자 ID [{}]가 파일 ID [{}] 정보를 삭제하려고 했으나 권한이 없습니다.", userDTO.getId(), postFileId);
            // 작성자가 아니면 권한 없음 예외 발생
            throw new UnauthorizedActionException(
                    "User with ID " + userDTO.getId() + " not authorized to delete info file with ID " + postFileId);
        }
        // 파일을 삭제 상태로 표시하고 데이터베이스에 저장
        postFile.setIsDeleted(true);
        PostFileDTO postFileDTO = postFileMapper.toDTO(postFileRepository.save(postFile));
        return postFileDTO;
    }

    /**
     * 특정 게시물을 찾는 메서드 (존재하지 않으면 예외 발생)
     * 
     * @param postId 게시물 ID
     * @return 게시물 엔티티
     */
    private Post findPostOrThrow(Long postId) {
        log.debug("게시물 ID [{}] 조회", postId);
        // 게시물 ID로 게시물을 조회하고, 없으면 예외 발생
        return postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> {
                    log.warn("게시물 ID [{}]를 찾을 수 없습니다.", postId);
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
        log.debug("파일 ID [{}] 조회", postFileId);
        // 파일 ID로 파일을 조회하고, 없으면 예외 발생
        return postFileRepository.findByIdAndIsDeletedFalse(postFileId)
                .orElseThrow(() -> {
                    log.warn("파일 ID [{}]를 찾을 수 없습니다.", postFileId);
                    return new ResourceNotFoundException("Post File not found with id: " + postFileId);
                });
    }

    /**
     * 고유한 파일 경로를 찾는 재귀 메서드
     * 
     * @param uploadPath 업로드 경로
     * @return 고유한 파일 경로
     */
    @Override
    public Path generateUniqueFilePath() {
        log.trace("파일명 생성 요청");
        String filename = UUID.randomUUID().toString().replace("-", "");
        Path uploadPath = Paths.get(uploadDir);
        Path filePath = uploadPath.resolve(filename);
        if (Files.exists(filePath)) {
            log.debug("중복 파일명 발생 - 새로운 파일명 생성 중");
            return generateUniqueFilePath();
        }
        return filePath;
    }
}
