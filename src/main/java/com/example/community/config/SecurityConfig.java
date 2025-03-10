package com.example.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 기능 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/users/**").permitAll() // 회원가입, 로그인 허용
                        .requestMatchers("/upload/**").permitAll() // 업로드 파일 접근 허용
                        .requestMatchers("/public/**").permitAll() // 정적 리소스 허용 추가
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                .formLogin(login -> login.disable()) // 기본 로그인 비활성화
                .httpBasic(basic -> basic.disable()); // HTTP Basic 인증 비활성화

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
