package com.safehouse.config;
import com.safehouse.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JwtConfig {
    @Value("${jwt.secret}") // application.properties에 설정할 시크릿 키
    private String secretKey;

    private final long tokenValidityInMilliseconds = 86400000; // 24시간

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(secretKey, tokenValidityInMilliseconds);
    }
}
