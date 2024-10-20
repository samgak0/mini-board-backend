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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.security.CustomAuthenticationProvider;
import shop.samgak.mini_board.security.CustomSecurityContextFilter;
import shop.samgak.mini_board.security.CustomSessionAuthentication;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final CustomSessionAuthentication customSessionAuthentication;
        private final CustomSecurityContextFilter customSecurityContextFilter;
        private final CustomAuthenticationProvider customAuthenticationProvider;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/api/auth/login",
                                                                "/api/users/check/**",
                                                                "/api/users/register",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/swagger-config",
                                                                "/v3/api-docs",
                                                                "/sessions",
                                                                "/sessions-redis")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .exceptionHandling(exceptionHandling -> exceptionHandling
                                                .authenticationEntryPoint(customSessionAuthentication))
                                .formLogin(form -> form.disable())
                                .logout(logout -> logout.disable())
                                .sessionManagement(session -> {
                                        session.maximumSessions(1)
                                                        .sessionRegistry(sessionRegistry());
                                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                                }).addFilterBefore(customSecurityContextFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public AuthenticationManager authManager(HttpSecurity http) throws Exception {
                AuthenticationManagerBuilder authenticationManagerBuilder = http
                                .getSharedObject(AuthenticationManagerBuilder.class);
                customAuthenticationProvider.setPasswordEncoder(passwordEncoder());
                authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
                return authenticationManagerBuilder.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        }

        @Bean
        public SessionRegistry sessionRegistry() {
                return new SessionRegistryImpl();
        }

        @Bean
        public HttpSessionEventPublisher httpSessionEventPublisher() {
                return new HttpSessionEventPublisher();
        }
}
