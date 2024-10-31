package shop.samgak.mini_board.config;

import java.io.IOException;
import java.time.Instant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Jackson 설정을 위한 구성 클래스입니다.
 * ObjectMapper를 전역적으로 설정하고 Instant 타입의 직렬화/역직렬화를 커스터마이징합니다.
 */
@Configuration
public class JacksonConfig {

    /**
     * 커스터마이즈된 ObjectMapper 빈을 생성합니다.
     * 이 ObjectMapper는 JavaTimeModule과 Jdk8Module을 등록하여 JDK8 날짜와 시간 유형을 처리하며,
     * Instant 타입을 유닉스 타임스탬프로 직렬화 및 역직렬화하는 기능을 추가합니다.
     * 
     * @return 설정된 ObjectMapper 객체
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Java의 날짜 및 시간 관련 타입을 지원하는 모듈을 등록합니다.
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());

        // Instant 타입을 유닉스 타임스탬프 형식으로 처리하는 직렬화 및 역직렬화 모듈 추가
        SimpleModule module = new SimpleModule();
        module.addSerializer(Instant.class, new UnixTimestampSerializer());
        module.addDeserializer(Instant.class, new UnixTimestampDeserializer());
        objectMapper.registerModule(module);

        return objectMapper;
    }

    /**
     * Instant 타입을 유닉스 타임스탬프(초)로 직렬화하는 커스텀 JsonSerializer 클래스입니다.
     */
    public class UnixTimestampSerializer extends JsonSerializer<Instant> {
        @Override
        public void serialize(Instant instant, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            // Instant 객체를 유닉스 타임스탬프 초 단위로 직렬화합니다.
            gen.writeNumber(instant.getEpochSecond());
        }
    }

    /**
     * 유닉스 타임스탬프(초)로부터 Instant 객체로 역직렬화하는 커스텀 JsonDeserializer 클래스입니다.
     */
    public class UnixTimestampDeserializer extends JsonDeserializer<Instant> {
        @Override
        public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            // 유닉스 타임스탬프 초 값을 Instant 객체로 변환합니다.
            return Instant.ofEpochSecond(p.getLongValue());
        }
    }
}
