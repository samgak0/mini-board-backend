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
import shop.samgak.mini_board.exceptions.ServerIOException;
import shop.samgak.mini_board.post.dto.PostFileDTO;
import shop.samgak.mini_board.post.services.PostFileService;
import shop.samgak.mini_board.user.dto.UserDTO;
import shop.samgak.mini_board.utility.ApiDataResponse;
import shop.samgak.mini_board.utility.ApiFailureResponse;
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

        @Value("${app.upload.uploadDir}")
        private String uploadDir;

        /**
         * 특정 게시물에 포함된 이미지 파일 목록을 가져오는 엔드포인트
         * 
         * @param postFileId 게시물 ID
         * @return 게시물에 포함된 파일 목록
         */
        // TODO : getPostFile 테스트 코드 추가
        @GetMapping("{postId}/images")
        public List<PostFileDTO> getPostFile(@PathVariable Long postId) {
                log.info("Request to get image files for Post ID: [{}]", postId);
                List<PostFileDTO> postFile = postFileService.getItemsByPost(postId);
                if (postFile.isEmpty()) {
                        log.warn("No files found for Post ID: [{}]", postId);
                        throw new ResourceNotFoundException("Post file not found with postId: " + postId);
                }
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
        public ResponseEntity<?> getPostFileAndPostFileId(@PathVariable Long postId,
                        @PathVariable Long postFileId,
                        HttpSession session) throws IOException {
                log.info("Request to get image file - Post ID: [{}], PostFile ID: [{}]", postId, postFileId);

                PostFileDTO postFileDTO = postFileService.getItem(postFileId, postId);
                Path uploadPath = Paths.get(uploadDir);
                Path filePath = uploadPath.resolve(postFileDTO.getFileName());

                // 파일 읽기 가능 여부 확인
                if (!Files.isReadable(filePath)) {
                        log.warn("Unable to read file at path: [{}]", filePath);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new ApiFailureResponse("Can not read this file"));
                }

                byte[] fileData = Files.readAllBytes(filePath);
                String uploadFileName = UriUtils.encode(postFileDTO.getOriginalName(), StandardCharsets.UTF_8);
                postFileService.increaseViewCount(postFileId, session);

                return ResponseEntity.ok()
                                .contentType(MediaType.parseMediaType(postFileDTO.getContentType()))
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=\"" + uploadFileName + "\"")
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
        public ResponseEntity<ApiDataResponse> uploadFile(@RequestParam MultipartFile file,
                        @PathVariable Long postId) {
                if (file.isEmpty()) {
                        log.warn("Uploaded file is empty - Post ID: [{}]", postId);
                        throw new MissingParameterException("file");
                }
                String filename = file.getOriginalFilename();
                String contentType = file.getContentType();
                long fileSize = file.getSize();

                // 현재 로그인된 사용자 정보 가져옴
                UserDTO userDTO = AuthUtils.getCurrentUser();
                log.info("Request to upload file - Post ID: [{}], User ID: [{}], Param name : [], File metadata [filename={}], [contentType={}], [fileSize={}]",
                                postId, userDTO.getId(), filename, contentType,
                                fileSize);

                // 고유 파일 이름 생성
                Path generateUniqueFilePath = postFileService.generateUniqueFilePath();

                // 파일 정보를 데이터베이스에 저장
                PostFileDTO postFileDTO = postFileService.writePostFileInfo(postId, filename,
                                generateUniqueFilePath.getFileName().toString(),
                                contentType,
                                fileSize, userDTO);

                // 파일 실제 이동
                postFileService.writePostFile(file, generateUniqueFilePath, postId);

                // 생성된 파일의 URI 반환
                URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/api/users/{id}")
                                .buildAndExpand(postFileDTO.getId())
                                .toUri();

                return ResponseEntity.created(location)
                                .body(new ApiDataResponse("File uploaded successfully", postFileDTO));
        }

        @DeleteMapping("{postId}/images/{postFileId}")
        public ResponseEntity<ApiDataResponse> deleteFile(@PathVariable Long postId,
                        @PathVariable Long postFileId) throws IOException {
                log.info("Request to delete file - postId: [{}], postFileId: [{}]", postId, postFileId);

                // 현재 로그인된 사용자 정보 가져옴
                UserDTO userDTO = AuthUtils.getCurrentUser();

                // 파일 정보 삭제
                PostFileDTO postFileDTO = postFileService.deleteFileInfo(postFileId, userDTO);

                // 파일 삭제 수행
                boolean isDeleted = postFileService.deleteFile(postFileDTO.getFileName());
                if (!isDeleted) {
                        String errorMessage = "File delete failed because the file does not exist - File ID ["
                                        + postFileDTO.getId() + "]";
                        log.error(errorMessage);
                        throw new ServerIOException(errorMessage);
                }

                return ResponseEntity.ok()
                                .body(new ApiDataResponse("File deleted successfully", postFileId));
        }
}
