package com.example.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 보호 기능 비활성화 (Postman 테스트용)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/users/**").permitAll() // 회원가입, 로그인은 인증 없이 허용
                    .requestMatchers("/uploads/**").permitAll()
                    .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
            )
            .formLogin(login -> login.disable()) // 기본 로그인 비활성화
            .httpBasic(basic -> basic.disable()); // HTTP Basic 인증 비활성화

        return http.build();
    }
}
