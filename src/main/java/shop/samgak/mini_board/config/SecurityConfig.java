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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.security.CustomSessionAuthentication;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final CustomSessionAuthentication customSessionAuthentication;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http.csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/api/users/login",
                                                                "/api/users/check/**",
                                                                "/api/users/register",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/swagger-config",
                                                                "/v3/api-docs",
                                                                "/sessions",
                                                                "/sessions-redis")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginProcessingUrl("/api/users/login")
                                                .successHandler(customSessionAuthentication)
                                                .failureHandler(customSessionAuthentication)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/api/users/logout")
                                                .logoutSuccessHandler(customSessionAuthentication))
                                .exceptionHandling(exceptionHandling -> exceptionHandling
                                                .authenticationEntryPoint(customSessionAuthentication))
                                .sessionManagement(session -> {
                                        session.maximumSessions(1)
                                                        .sessionRegistry(sessionRegistry());
                                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                                })
                                .headers(headers -> headers
                                                .frameOptions(frameOptions -> frameOptions.disable())
                                                .xssProtection(xssOptions -> xssOptions.disable())
                                                .cacheControl(cacheOptions -> cacheOptions.disable())
                                                .contentTypeOptions(
                                                                contentTypeOptions -> contentTypeOptions.disable()));

                return http.build();
        }

        @Bean
        public AuthenticationManager authManager(HttpSecurity http) throws Exception {
                AuthenticationManagerBuilder authenticationManagerBuilder = http
                                .getSharedObject(AuthenticationManagerBuilder.class);
                authenticationManagerBuilder
                                .userDetailsService(customSessionAuthentication)
                                .passwordEncoder(passwordEncoder());
                return authenticationManagerBuilder.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
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
