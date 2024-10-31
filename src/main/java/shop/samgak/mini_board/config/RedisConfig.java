package shop.samgak.mini_board.config;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.NonNull;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

/**
 * RedisConfig 클래스는 Spring Boot 애플리케이션에서 Redis를 사용하여 세션을 관리하기 위한 설정을 정의합니다.
 * 이 클래스는 RedisTemplate과 RedisSerializer를 정의하고, Redis를 사용한 HTTP 세션 관리를 활성화합니다.
 */
@Configuration
@EnableRedisHttpSession // Redis를 사용하여 HTTP 세션을 관리하도록 활성화
public class RedisConfig implements BeanClassLoaderAware {

	private ClassLoader loader;

	/**
	 * Spring Session에서 기본적으로 사용하는 Redis Serializer를 정의합니다.
	 * 이 Serializer는 세션 객체를 Redis에 저장할 때 JSON 형식으로 직렬화하여 저장합니다.
	 * 
	 * @return RedisSerializer<Object> - 세션을 Redis에 저장할 때 JSON 형식으로 직렬화하기 위한
	 *         Serializer
	 */
	@Bean
	public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
		return new GenericJackson2JsonRedisSerializer(objectMapper()); // ObjectMapper를 이용하여 JSON 형식으로 직렬화
	}

	/**
	 * RedisTemplate을 정의합니다. 이 Template은 Redis와 상호작용하는 데 사용됩니다.
	 * RedisTemplate은 Redis 서버에 데이터를 저장하고 가져오는 데 사용되며, 키와 값의 직렬화 방식을 설정할 수 있습니다.
	 * 
	 * @param connectionFactory Redis 연결을 위한 ConnectionFactory
	 * @return RedisTemplate<String, Object> - Redis와 데이터를 주고받기 위한 Template
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory); // Redis 연결 설정

		// Redis 키와 값을 직렬화하는 방식 설정
		template.setKeySerializer(new StringRedisSerializer()); // 키를 문자열로 직렬화
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper())); // 값을 JSON으로 직렬화
		template.setHashKeySerializer(new StringRedisSerializer()); // 해시의 키를 문자열로 직렬화
		template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper())); // 해시의 값을 JSON으로 직렬화

		template.afterPropertiesSet(); // Template 설정 완료 후 초기화 작업 수행
		return template;
	}

	/**
	 * BeanClassLoaderAware 인터페이스를 구현하여 Bean의 ClassLoader를 설정합니다.
	 * Spring Security 모듈과 ObjectMapper의 직렬화 및 역직렬화를 위해 ClassLoader를 사용합니다.
	 * 
	 * @param classLoader 이 빈의 ClassLoader
	 */
	@Override
	public void setBeanClassLoader(@NonNull ClassLoader classLoader) {
		this.loader = classLoader;
	}

	/**
	 * ObjectMapper를 정의합니다. 이 Mapper는 Redis에서 객체를 직렬화 및 역직렬화할 때 사용됩니다.
	 * 기본 타입 활성화를 설정하여 객체의 타입 정보를 유지하며, Spring Security 모듈을 등록하여 보안 관련 객체의 직렬화 및
	 * 역직렬화가 가능하도록 합니다.
	 * 
	 * 타입 정보를 포함하기 위해 Bean으로 등록된 ObjectMapper와 별도로 설정합니다.
	 * 
	 * @return ObjectMapper - 직렬화 및 역직렬화를 위한 설정된 ObjectMapper
	 */
	private ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		// 기본 타입 활성화 설정을 통해 객체 타입 정보를 유지하여 역직렬화 시 올바른 타입을 확인 가능하도록 설정
		objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
				JsonTypeInfo.As.PROPERTY);
		// Spring Security 모듈을 ObjectMapper에 등록하여 보안 관련 객체도 직렬화 및 역직렬화 가능하도록 설정
		objectMapper.registerModules(SecurityJackson2Modules.getModules(this.loader));

		return objectMapper;
	}
}
