package com.safehouse.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@Component
public class JwtTokenProvider {

    private final String secretKey;
    private final long validityInMilliseconds;
    private final UserDetailsService userDetailsService;
    private final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            @Value("${jwt.token-validity-in-seconds}") long validityInSeconds,
                            UserDetailsService userDetailsService) {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.validityInMilliseconds = validityInSeconds * 1000L;
        this.userDetailsService = userDetailsService;
    }

    // 토큰 생성
    public String createToken(String email, String role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role); // 사용자 역할 저장
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            if (isTokenBlacklisted(token)) {
                return false;
            }
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 이메일 추출 메소드
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰을 블랙리스트에 추가 (로그아웃)
    public void addToBlacklist(String token) {
        tokenBlacklist.add(token);
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }
    // 요청에서 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 인증 정보 생성
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getEmailFromToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
    /*
     * JWT 토큰을 HTTP-Only 쿠키에 추가하는 메서드
     * @param token JWT 토큰
     * @param response HTTP 응답 객체
     */
    public void addTokenToCookie(String token, HttpServletResponse response) {
        // HttpOnly 쿠키에 토큰 저장 (보안용)
        // HTTP-Only 쿠키 생성
        Cookie cookie = new Cookie("JWT_TOKEN", token);

        // 쿠키 보안 설정
        cookie.setHttpOnly(true);  // JavaScript로 쿠키 접근 방지
        cookie.setSecure(true);    // HTTPS에서만 쿠키 전송
        cookie.setPath("/");       // 전체 애플리케이션에서 쿠키 사용

        // 쿠키 만료 시간을 토큰 만료 시간과 동일하게 설정
        cookie.setMaxAge((int) (validityInMilliseconds / 1000));

        // 응답에 쿠키 추가
        response.addCookie(cookie);

         // 인증 상태 쿠키 추가 (프론트엔드용)
        Cookie isAuthenticatedCookie = new Cookie("isAuthenticated", "true");
        isAuthenticatedCookie.setHttpOnly(false);
        isAuthenticatedCookie.setSecure(true);
        isAuthenticatedCookie.setPath("/");
        isAuthenticatedCookie.setMaxAge((int) (validityInMilliseconds / 1000));
        response.addCookie(isAuthenticatedCookie);
    }

    /*
     * 로그아웃 시 쿠키를 만료시키는 메서드
     * @param response HTTP 응답 객체
     */
    public void invalidateCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // 쿠키 즉시 만료
        response.addCookie(cookie);
    }


}
