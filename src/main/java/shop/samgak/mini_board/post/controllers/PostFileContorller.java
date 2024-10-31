package shop.samgak.mini_board.post.controllers;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import shop.samgak.mini_board.post.dto.PostFileDTO;
import shop.samgak.mini_board.post.services.PostFileService;
import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.utility.ApiDataResponse;
import shop.samgak.mini_board.utility.ApiResponse;
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
    private String uploadDir;

    /**
     * 특정 게시물에 포함된 이미지 파일 목록을 가져오는 엔드포인트
     * 
     * @param postFileId 게시물 ID
     * @return 게시물에 포함된 파일 목록
     */
    // TODO : getPostFile 테스트 코드 추가
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
    // TODO : getPostFileAndPostFileId 테스트 코드 추가
    @GetMapping("{postId}/images/{postFileId}")
    public ResponseEntity<?> getPostFileAndPostFileId(@PathVariable("postId") Long postId,
            @PathVariable("postFileId") Long postFileId,
            HttpSession session) throws IOException {
        log.info("이미지 파일 요청 - postId: [{}], postFileId: [{}]", postId, postFileId);
        // 파일 정보를 가져옴
        PostFileDTO postFileDTO = postFileService.getItem(postFileId, postId);
        Path uploadPath = Paths.get(uploadDir);
        Path filePath = uploadPath.resolve(postFileDTO.getFileName());

        if (!Files.isReadable(filePath)) {
            log.warn("해당 파일을 읽어올 수 없습니다. - filePath: [{}]", filePath);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Can not read this element", ApiResponse.Code.FAILURE));
        }

        byte[] fileData = Files.readAllBytes(filePath);

        // 파일명 인코딩 처리
        String uploadFileName = UriUtils.encode(postFileDTO.getOriginalName(), StandardCharsets.UTF_8);

        // 조회수 증가 처리
        log.debug("조회수 증가 처리 중 - postFileId: [{}]", postFileId);
        postFileService.increaseViewCount(postFileId, session);

        // 파일 정보와 데이터 반환
        log.info("이미지 파일 출력 - 파일명: [{}], 파일 크기: [{}] bytes, ContentType = [{}]", uploadFileName, fileData.length,
                postFileDTO.getContentType());
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
        if (file.isEmpty()) {
            log.warn("업로드된 파일이 비어 있음 - postId: [{}]", postId);
            throw new MissingParameterException("file");
        }
        log.info("파일 업로드 요청 - postId: [{}], 파일명: [{}]", postId, file.getOriginalFilename());
        // 현재 로그인된 사용자 정보 가져옴
        UserDTO userDTO = AuthUtils.getCurrentUser();
        log.info("파일 업로드 시도 시도 사용자  - userId: [{}], 사용자이름: [{}]", userDTO.getId(), userDTO.getUsername());

        // 고유 파일 이름 생성
        Path generateUniqueFilePath = postFileService.generateUniqueFilePath();
        log.debug("서버 파일 명 생성 - [{}]", generateUniqueFilePath.toString());

        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();
        long fileSize = file.getSize();
        log.trace("파일 메타데이터 [filename={}], [contentType={}], [fileSize={}]", filename, contentType, fileSize);

        // 파일 정보를 데이터베이스에 저장
        PostFileDTO postFileDTO = postFileService.writePostFileInfo(postId, filename,
                generateUniqueFilePath.getFileName().toString(),
                contentType,
                fileSize, userDTO);

        log.trace("파일 메타데이터 쓰기 성공");

        // 파일 실제 이동
        postFileService.writePostFile(file, generateUniqueFilePath, postId);

        log.trace("파일 실제 쓰기 성공");

        // 생성된 파일의 URI 반환
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/users/{id}")
                .buildAndExpand(postFileDTO.getId())
                .toUri();

        log.info("파일 업로드 성공 - postId: [{}], 파일 ID: [{}]", postId, postFileDTO.getId());
        return ResponseEntity.created(location)
                .body(new ApiDataResponse("File uploaded successfully", postFileDTO, true));
    }

    @DeleteMapping("{postId}/images/{postFileId}")
    public ResponseEntity<ApiDataResponse> deleteFile(@PathVariable("postId") Long postId,
            @PathVariable("postFileId") Long postFileId) {
        // 현재 로그인된 사용자 정보 가져옴
        UserDTO userDTO = AuthUtils.getCurrentUser();
        PostFileDTO postFileDTO = postFileService.deleteFileInfo(postFileId, userDTO);
        log.info("파일 ID [{}]의 정보가 성공적으로 삭제 처리되었습니다.", postFileId);
        // 파일 삭제 수행
        postFileService.deleteFile(postFileDTO.getFileName());
        log.info("파일 ID [{}], fileName: [{}] 을 물리적으로 삭제 성공", postFileDTO.getId(), postFileDTO.getFileName());
        return ResponseEntity.ok()
                .body(new ApiDataResponse("File delected successfully", postFileId, true));
    }
}
