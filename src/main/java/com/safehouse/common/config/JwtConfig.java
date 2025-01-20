package com.safehouse.common.config;
import com.safehouse.common.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;


@Configuration
public class JwtConfig {
    @Value("${jwt.secret}") // application.properties에 설정할 시크릿 키
    private String secretKey;

    private final long tokenValidityInMilliseconds = 86400000; // 24시간

    private final UserDetailsService userDetailsService;

    public JwtConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(secretKey, tokenValidityInMilliseconds, userDetailsService);
    }
}
