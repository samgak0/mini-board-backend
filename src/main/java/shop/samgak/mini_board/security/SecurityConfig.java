package shop.samgak.mini_board.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import shop.samgak.mini_board.security.component.CustomAuthenticationEntryPoint;
import shop.samgak.mini_board.security.component.CustomAuthenticationFailureHandler;
import shop.samgak.mini_board.security.component.CustomAuthenticationSuccessHandler;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
        private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
        private final CustomUserDetailsService userDetailsService;
        private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/api/users/login", "/api/users/register").permitAll()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginProcessingUrl("/api/users/login")
                                                .successHandler(customAuthenticationSuccessHandler)
                                                .failureHandler(customAuthenticationFailureHandler)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/api/users/logout"))
                                .exceptionHandling(exceptionHandling -> exceptionHandling
                                                .authenticationEntryPoint(customAuthenticationEntryPoint))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
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
                                .userDetailsService(userDetailsService)
                                .passwordEncoder(passwordEncoder());
                return authenticationManagerBuilder.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(); // 비밀번호 암호화 인코더 등록
        }
}
