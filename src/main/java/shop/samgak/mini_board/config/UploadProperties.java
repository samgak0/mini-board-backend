package shop.samgak.mini_board.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {
    private String uploadDir;
    private int maxRetry;
}
