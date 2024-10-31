package shop.samgak.mini_board.post.controllers;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.exceptions.MissingParameterException;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.exceptions.ServerIOException;
import shop.samgak.mini_board.post.dto.PostFileDTO;
import shop.samgak.mini_board.post.services.PostFileService;
import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.utility.ApiDataResponse;
import shop.samgak.mini_board.utility.AuthUtils;

/**
 * 게시물 파일 관련 API 요청을 처리하는 컨트롤러 정의
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts/")
public class PostFileContorller {

    final PostFileService postFileService;

    @Value("${app.uploadDir}")
    private String UPLOAD_DIR;

    /**
     * 특정 게시물에 포함된 이미지 파일 목록을 가져오는 엔드포인트
     * 
     * @param postFileId 게시물 ID
     * @return 게시물에 포함된 파일 목록
     */
    @GetMapping("{postId}/images")
    public List<PostFileDTO> getPostFile(@PathVariable("postId") Long postFileId) {
        log.info("이미지 파일 목록 요청 - postId: [{}]", postFileId);
        List<PostFileDTO> postFile = postFileService.getItemByPost(postFileId);
        if (postFile.isEmpty()) {
            log.warn("게시물 파일을 찾을 수 없음 - postId: [{}]", postFileId);
            throw new ResourceNotFoundException("Post file not found with postId: " + postFileId);
        }
        log.info("이미지 파일 목록 반환 - postId: [{}], 파일 수: [{}]", postFileId, postFile.size());
        return postFile;
    }

    /**
     * 특정 게시물의 이미지 파일을 가져오는 엔드포인트
     * 
     * @param postId     게시물 ID
     * @param postFileId 파일 ID
     * @param session    현재 세션 객체 (조회수 증가용)
     * @return 이미지 파일의 바이트 데이터와 메타데이터 응답
     * @throws IOException 파일을 읽을 수 없을 때 예외 발생
     */
    @GetMapping("{postId}/images/{imageIndex}")
    public ResponseEntity<byte[]> getPostFileAndImageIndex(@PathVariable("postId") Long postId,
            @PathVariable("postFileId") Long postFileId,
            HttpSession session) throws IOException {
        log.info("이미지 파일 요청 - postId: [{}], postFileId: [{}]", postId, postFileId);
        // 파일 정보를 가져옴
        PostFileDTO postFileDTO = postFileService.getItem(postFileId, postFileId);
        Path uploadPath = Paths.get(UPLOAD_DIR);
        Path filePath = uploadPath.resolve(postFileDTO.getFileName());
        byte[] fileData = Files.readAllBytes(filePath);

        // 파일명 인코딩 처리
        String uploadFileName = UriUtils.encode(postFileDTO.getOriginalName(), StandardCharsets.UTF_8);

        // 조회수 증가 처리
        log.debug("조회수 증가 처리 중 - postFileId: [{}]", postFileId);
        postFileService.increaseViewCount(postFileId, session);

        // 파일 정보와 데이터 반환
        log.info("이미지 파일 반환 - 파일명: [{}], 파일 크기: [{}] bytes", uploadFileName, fileData.length);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(postFileDTO.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + uploadFileName + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileData.length))
                .body(fileData);
    }

    /**
     * 특정 게시물에 파일을 업로드하는 엔드포인트
     * 
     * @param file   업로드할 파일
     * @param postId 게시물 ID
     * @return 업로드된 파일의 정보와 URI 응답
     */
    @PostMapping("{postId}/images")
    public ResponseEntity<ApiDataResponse> uploadFile(@RequestParam("file") MultipartFile file,
            @PathVariable("postId") Long postId) {
        log.info("파일 업로드 요청 - postId: [{}], 파일명: [{}]", postId, file.getOriginalFilename());
        // 현재 로그인된 사용자 정보 가져옴
        UserDTO userDTO = AuthUtils.getCurrentUser();

        if (file.isEmpty()) {
            log.warn("업로드된 파일이 비어 있음 - postId: [{}]", postId);
            throw new MissingParameterException("file");
        }
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();
        long fileSize = file.getSize();

        try {
            // 업로드 디렉토리 확인 및 생성
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                log.debug("업로드 디렉토리가 존재하지 않음 - 디렉토리 생성 중: [{}]", UPLOAD_DIR);
                Files.createDirectories(uploadPath);
            }
            // 고유한 파일 경로 찾기
            Path randomPath = findUniqueFilePath(uploadPath);
            // 파일을 디스크에 저장
            file.transferTo(randomPath.toFile());
            log.info("파일 저장 완료 - 경로: [{}]", randomPath);
            // 파일 정보를 데이터베이스에 저장
            PostFileDTO postFileDTO = postFileService.writePostFileInfo(postId, filename,
                    randomPath.getFileName().toString(),
                    contentType,
                    fileSize, userDTO);

            // 생성된 파일의 URI 반환
            URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/users/{id}")
                    .buildAndExpand(postFileDTO.getId())
                    .toUri();

            log.info("파일 업로드 성공 - postId: [{}], 파일 ID: [{}]", postId, postFileDTO.getId());
            return ResponseEntity.created(location)
                    .body(new ApiDataResponse("File uploaded successfully", postFileDTO, true));

        } catch (IOException e) {
            log.error("파일 업로드 실패 - postId: [{}], 오류: [{}]", postId, e.getMessage());
            throw new ServerIOException();
        }
    }

    /**
     * 고유한 파일 경로를 찾는 재귀 메서드
     * 
     * @param uploadPath 업로드 경로
     * @return 고유한 파일 경로
     */
    private Path findUniqueFilePath(Path uploadPath) {
        String filename = UUID.randomUUID().toString().replace("-", "");
        Path filePath = uploadPath.resolve(filename);
        if (Files.exists(filePath)) {
            log.debug("중복 파일명 발생 - 새로운 파일명 생성 중");
            return findUniqueFilePath(uploadPath);
        }
        return filePath;
    }

    // TODO : 파일 삭제 구현
}
