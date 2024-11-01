package shop.samgak.mini_board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.security.UnauthenticationEntryPoint;
import shop.samgak.mini_board.security.CustomAuthenticationProvider;

/**
 * SecurityConfig 클래스는 Spring Security의 설정을 정의합니다.
 * 이 클래스는 애플리케이션의 보안 구성을 위한 다양한 빈(bean)과 설정을 제공합니다.
 * 사용자 인증, 권한 부여, 세션 관리, 그리고 기타 보안 관련 설정을 포함합니다.
 */
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        // 인증되지 않은 접근 시 처리할 Custom Authentication Entry Point
        private final UnauthenticationEntryPoint unauthenticationEntryPoint;
        // 커스텀 인증 로직을 처리할 Custom Authentication Provider
        private final CustomAuthenticationProvider customAuthenticationProvider;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                // CSRF 보호 비활성화 (주로 상태 없는 API에 사용)
                http.csrf(csrf -> csrf.disable())
                                // 권한 설정 구성
                                .authorizeHttpRequests(authorize -> authorize
                                                // 특정 엔드포인트는 모든 사용자에게 접근 허용 (예: 로그인, 회원가입, Swagger 문서)
                                                .requestMatchers("/api/auth/login",
                                                                "/api/users/check/**",
                                                                "/api/users/register",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/swagger-config",
                                                                "/v3/api-docs",
                                                                "/sessions",
                                                                "/sessions-redis",
                                                                "/actuator/**")
                                                .permitAll() // 이 엔드포인트들은 인증 없이 접근 가능
                                                .anyRequest().authenticated()) // 그 외의 모든 요청은 인증 필요
                                // 인증되지 않은 접근 시 커스텀 엔트리 포인트 사용
                                .exceptionHandling(exceptionHandling -> exceptionHandling
                                                .authenticationEntryPoint(unauthenticationEntryPoint))
                                // 기본 폼 로그인 비활성화
                                .formLogin(form -> form.disable())
                                // 기본 로그아웃 처리 비활성화
                                .logout(logout -> logout.disable())
                                // 세션 관리 설정
                                .sessionManagement(session -> {
                                        // 사용자당 최대 세션 수 1로 제한
                                        session.maximumSessions(1)
                                                        .sessionRegistry(sessionRegistry()); // 세션을 추적하기 위해 커스텀 세션 레지스트리
                                                                                             // 사용
                                        // 세션 생성 정책 설정 (필요 시 세션 생성)
                                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                                });

                return http.build();
        }

        @Bean
        public AuthenticationManager authManager(HttpSecurity http) throws Exception {
                // HttpSecurity에서 AuthenticationManagerBuilder 가져오기
                AuthenticationManagerBuilder authenticationManagerBuilder = http
                                .getSharedObject(AuthenticationManagerBuilder.class);
                // 커스텀 인증 제공자에 패스워드 인코더 설정
                customAuthenticationProvider.setPasswordEncoder(passwordEncoder());
                // AuthenticationManagerBuilder에 커스텀 인증 제공자 등록
                authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
                return authenticationManagerBuilder.build(); // AuthenticationManager를 빌드하여 반환
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                // 여러 인코딩 유형을 지원하는 DelegatingPasswordEncoder 사용
                return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        }

        @Bean
        public SessionRegistry sessionRegistry() {
                // 동시성 제어를 위해 세션을 추적하는 빈
                return new SessionRegistryImpl();
        }

        @Bean
        public HttpSessionEventPublisher httpSessionEventPublisher() {
                // 세션 이벤트를 게시하는 빈, 세션 동시성 제어에 유용함
                return new HttpSessionEventPublisher();
        }
}
