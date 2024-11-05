package shop.samgak.mini_board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

/**
 * 애플리케이션의 전역 설정을 관리하는 클래스입니다.
 * 이 클래스는 RestTemplate과 같은 빈을 정의하여 다른 컴포넌트에서 사용할 수 있도록 합니다.
 */
@Configuration
public class AppConfig {

    /**
     * RestTemplate 빈을 생성합니다.
     * RestTemplate은 HTTP 요청을 수행하기 위한 Spring의 유틸리티 클래스입니다.
     * 다른 서비스나 API와 통신할 때 사용됩니다.
     *
     * @return RestTemplate 인스턴스
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:8080") // 기본 URL 설정
                .build();
    }
}
