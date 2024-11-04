package shop.samgak.mini_board.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {
    /**
     * 파일 업로드 경로를 나타내는 설정값입니다.
     */
    private String uploadDir;
    /**
     * 업로드 실패 시 최대 재시도 횟수를 설정하는 값입니다.
     */
    private int maxRetry;
}