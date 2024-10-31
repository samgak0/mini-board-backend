package shop.samgak.mini_board.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

/**
 * 테스트에서 Mock 사용자 인증을 설정하기 위한 커스텀 어노테이션 정의
 */
@WithSecurityContext(factory = WithMockMyUserDetailsSecurityContextFactory.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithMockMyUserDetails {
    /**
     * 사용자 이름 설정 (기본값: "user")
     * 
     * @return 사용자 이름
     */
    String username() default "user";

    /**
     * 사용자 ID 설정 (기본값: 1L)
     * 
     * @return 사용자 ID
     */
    long id() default 1L;
}
