package shop.samgak.mini_board.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private boolean debug = false;
}
