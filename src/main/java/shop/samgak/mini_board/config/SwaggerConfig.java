package shop.samgak.mini_board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * SwaggerConfig 클래스는 애플리케이션의 Swagger 설정을 정의합니다.
 * 이 클래스는 SpringDoc을 사용하여 API 문서를 자동으로 생성하고, Swagger UI에서 API 정보를 볼 수 있도록 합니다.
 * OpenAPI 객체를 구성하여 API에 대한 메타데이터를 설정하고 구성 요소를 정의합니다.
 */
@Configuration
public class SwaggerConfig {
    /**
     * OpenAPI Bean을 정의합니다. Swagger UI에 사용할 OpenAPI 객체를 생성합니다.
     * 이 메서드는 Swagger를 사용하여 API 문서를 자동으로 생성하는 데 필요한 설정을 제공합니다.
     * 
     * @return OpenAPI 객체로, API 문서에 대한 메타데이터와 구성 요소들을 포함합니다.
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()) // API 문서의 구성 요소를 정의합니다. 현재는 빈 Components 객체를 사용합니다.
                .info(apiInfo()); // API의 기본 정보를 설정합니다.
    }

    /**
     * API 정보(Info 객체)를 정의합니다.
     * API의 제목, 설명, 버전 등의 메타데이터를 설정합니다.
     * 
     * @return Info 객체로, API의 기본 정보를 포함합니다.
     */
    private Info apiInfo() {
        return new Info()
                .title("API Test") // API의 제목을 설정합니다.
                .description("Swagger UI") // API에 대한 설명을 설정합니다.
                .version("1.0.0"); // API의 버전을 설정합니다.
    }
}
