package com.safehouse.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.safehouse.common.security.JwtAuthenticationFilter;
import com.safehouse.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//    private final JwtTokenProvider jwtTokenProvider;
//
//    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
//        this.jwtTokenProvider = jwtTokenProvider;
//    }
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        // 기능 API
//                        .requestMatchers(
//                                "/api/users/**",     // 회원가입, 이메일 인증 등 모든 사용자 관련 API
//                                "/api/auth/**",      // 로그인 등 인증 관련 API
//                                "/api/reports",     // 신고 관련 API
//                                "/api/health",       // 서버 상태 확인 API
//                                "api/resident_communities/**", //커뮤니티 관련 API
//                                "/api/schedules/**",  // 일정관리 API
//                                "/api/health",      // 서버 상태 확인 API
//                                "/api/inspector_communities/**",
//                                "/api/model/**",            // model
//                                "api/inspections/**",       // 점검 API
//                                "api/dashboard/**"          // 대시보드
//                        ).permitAll()
//                        // 정적 리소스
//                        .requestMatchers(
//                                "/",
//                                "/error",
//                                "/favicon.ico",
//                                "/**/*.png",
//                                "/**/*.gif",
//                                "/**/*.svg",
//                                "/**/*.jpg",
//                                "/**/*.html",
//                                "/**/*.css",
//                                "/**/*.js"
//                        ).permitAll()
//                        // 역할 기반 접근 제어
//                        .requestMatchers("/api/inspector/**").hasAnyAuthority("ROLE_INSPECTOR", "ROLE_ADMIN")
//                        .requestMatchers("/api/resident/**").hasAuthority("ROLE_RESIDENT")
//                        // 로그아웃은 인증된 사용자만
//                        .requestMatchers("/api/auth/logout").authenticated()
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
//                        UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://safehouse-react-a5eyc2a9a0byd5hq.koreacentral-01.azurewebsites.net")); // React 서버
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//    //BCrypt 암호화 인스턴스 생성
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(10);
//    }
//}

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // JWT 필터를 먼저 적용
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/users/**",
                                "/api/auth/**",
                                "/api/reports/**",
                                "/api/health",
                                "/api/resident_communities/**",
                                "/api/schedules/**",
                                "/api/health",
                                "/api/inspector_communities/**",
                                "/api/model/**",
                                "/api/inspections/**",
                                "/api/dashboard/**"
                        ).permitAll()
                        .requestMatchers(
                                "/",
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
                        .requestMatchers("/api/inspector/**").hasAnyAuthority("ROLE_INSPECTOR", "ROLE_ADMIN")
                        .requestMatchers("/api/resident/**").hasAuthority("ROLE_RESIDENT")
                        .requestMatchers("/api/resident/**").hasAuthority("RESIDENT")
                        .requestMatchers("/api/auth/logout").authenticated()
                        .anyRequest().authenticated()
                )
                // 익명 인증 비활성화 추가
                .anonymous(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://safehouse-react-a5eyc2a9a0byd5hq.koreacentral-01.azurewebsites.net"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        // Authorization 헤더 노출 설정 추가
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}