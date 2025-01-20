package com.safehouse.common.config;
import com.safehouse.common.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;


@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secretKey;

    // 기본값 설정 추가
    @Value("${jwt.token-validity-in-seconds:86400}")
    private long validityInSeconds;


    @Bean
    public JwtTokenProvider jwtTokenProvider(UserDetailsService userDetailsService) {
        return new JwtTokenProvider(secretKey, validityInSeconds * 1000, userDetailsService);
    }
}

