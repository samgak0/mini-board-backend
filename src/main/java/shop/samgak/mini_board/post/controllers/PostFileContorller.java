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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.exceptions.MissingParameterException;
import shop.samgak.mini_board.exceptions.ResourceNotFoundException;
import shop.samgak.mini_board.exceptions.ServerIOException;
import shop.samgak.mini_board.post.dto.PostFileDTO;
import shop.samgak.mini_board.post.services.PostFileService;
import shop.samgak.mini_board.utility.ApiDataResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts/")
public class PostFileContorller {

    final PostFileService postFileService;

    @Value("${app.uploadDir}")
    private String UPLOAD_DIR;

    @GetMapping("{postId}/images")
    public List<PostFileDTO> getPostFile(@PathVariable("postId") Long postFileId) {
        List<PostFileDTO> postFile = postFileService.getItemByPost(postFileId);
        if (postFile.isEmpty()) {
            throw new ResourceNotFoundException("Post file not found with postId: "
                    + postFileId);
        }
        return postFile;
    }

    @GetMapping("{postId}/images/{imageIndex}")
    public ResponseEntity<byte[]> getPostFileAndImageIndex(@PathVariable("postId") Long postFileId,
            @PathVariable("imageIndex") Long imageIndex) throws IOException {
        PostFileDTO postFileDTO = postFileService.getItem(postFileId, imageIndex)
                .orElseThrow(() -> new ResourceNotFoundException("Post file not found with postId: "
                        + postFileId + " and imageIndex: " + imageIndex));
        Path uploadPath = Paths.get(UPLOAD_DIR);
        Path filePath = uploadPath.resolve(postFileDTO.getFileName());
        byte[] fileData = Files.readAllBytes(filePath);

        String uploadFileName = UriUtils.encode(postFileDTO.getOriginalName(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(postFileDTO.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + uploadFileName + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileData.length))
                .body(fileData);
    }

    @PostMapping("{postId}/images")
    public ResponseEntity<ApiDataResponse> uploadFile(@RequestParam("file") MultipartFile file,
            @PathVariable("postId") Long postId) {

        if (file.isEmpty()) {
            throw new MissingParameterException("file");
        }
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();
        long fileSize = file.getSize();

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path randomPath = findUniqueFilePath(uploadPath);
            file.transferTo(randomPath.toFile());
            PostFileDTO postFileDTO = postFileService.writePostFileInfo(postId, filename,
                    randomPath.getFileName().toString(),
                    contentType,
                    fileSize);

            URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/users/{id}")
                    .buildAndExpand(postFileDTO.getId())
                    .toUri();

            return ResponseEntity.created(location)
                    .body(new ApiDataResponse("File uploaded successfully", postFileDTO, true));

        } catch (IOException e) {
            throw new ServerIOException();
        }
    }

    private Path findUniqueFilePath(Path uploadPath) {
        String filename = UUID.randomUUID().toString().replace("-", "");
        Path filePath = uploadPath.resolve(filename);
        if (Files.exists(filePath)) {
            return findUniqueFilePath(uploadPath);
        }
        return filePath;
    }
}
