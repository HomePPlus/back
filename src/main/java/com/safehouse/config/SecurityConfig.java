package com.safehouse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.safehouse.security.JwtAuthenticationFilter;
import com.safehouse.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 회원가입 관련 엔드포인트
                        .requestMatchers(
                                "/api/signup/step1/**",  // 약관동의
                                "/api/signup/step2/**",  // 본인확인
                                "/api/signup/step3/**",  // 정보입력
                                "/api/signup/step4/**",  // 가입완료
                                "/api/users/resident/join",
                                "/api/users/inspector/join",
                                "/api/users/verify",        // 이메일 토큰
                                "/api/users/check-email",    // 이메일 중복확인
                                "/api/users/send-verification",  // 이메일 인증코드 받기
                                "/login"                   //로그인
                        ).permitAll()
                        // 정적 리소스
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/login.html",
                                "/error",
                                "/favicon.ico",
                                "/**/*.png",
                                "/**/*.gif",
                                "/**/*.svg",
                                "/**/*.jpg",
                                "/**/*.html",
                                "/**/*.css",
                                "/**/*.js"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.configure(http));

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        try {
            return new BCryptPasswordEncoder(10);
        } catch (Exception e) {
            throw new RuntimeException("BCrypt 암호화 인스턴스 생성 실패", e);
        }
    }


}
