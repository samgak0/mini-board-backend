package shop.samgak.mini_board.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@WithSecurityContext(factory = WithMockMyUserDetailsSecurityContextFactory.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithMockMyUserDetails {
    String username() default "user";

    long id() default 1L;
}