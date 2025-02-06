package com.safehouse.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
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
    public String createToken(String email, String role, Long userId) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        claims.put("userId", userId);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            if (isTokenBlacklisted(token)) {
                System.out.println("Token is blacklisted");
                return false;
            }
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))  // createToken과 동일한 방식으로 수정
                    .build()
                    .parseClaimsJws(token);

            boolean isValid = !claims.getBody().getExpiration().before(new Date());
            System.out.println("Token validation result: " + isValid);
            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Token validation failed: " + e.getMessage());
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

    public String resolveToken(HttpServletRequest request) {
        // 헤더에서 토큰 찾기
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7).trim();
            System.out.println("Found token in header: " + token);
            return token;
        }

        // 쿠키에서 토큰 찾기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    System.out.println("Found token in cookie: " + cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        System.out.println("No token found in request");
        return null;
    }

    public Authentication getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            // DB에서 사용자 정보 조회
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 토큰의 role과 DB의 role이 일치하는지 확인
            if (!userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_" + role))) {
                throw new JwtException("Invalid role in token");
            }

            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        } catch (Exception e) {
            throw new JwtException("Failed to authenticate token");
        }
    }
    /*
     * JWT 토큰을 HTTP-Only 쿠키에 추가하는 메서드
     * @param token JWT 토큰
     * @param response HTTP 응답 객체
     */
    public void addTokenToCookie(String token, HttpServletResponse response) {
        // 환경에 따른 도메인 설정
        String domain = isProduction() ?
                "koreacentral-01.azurewebsites.net" :
                "localhost";

        // JWT 토큰 쿠키 생성
        Cookie cookie = new Cookie("JWT_TOKEN", token);
        cookie.setHttpOnly(false);
        cookie.setSecure(isProduction());  // 프로덕션에서만 Secure 설정
        cookie.setPath("/");
        cookie.setDomain(domain);
        cookie.setMaxAge((int) (validityInMilliseconds / 1000));

        // 쿠키 헤더 설정
        response.addHeader("Set-Cookie", String.format(
                "JWT_TOKEN=%s; Max-Age=%d; Path=/; Domain=%s; %sHttpOnly; SameSite=%s",
                token,
                (int) (validityInMilliseconds / 1000),
                domain,
                isProduction() ? "Secure; " : "",
                isProduction() ? "None" : "Lax"
        ));

        response.addCookie(cookie);
    }
    /*
     * 로그아웃 시 쿠키를 만료시키는 메서드
     * @param response HTTP 응답 객체
     */
    public void invalidateCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT_TOKEN", null);
        cookie.setHttpOnly(false);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // 쿠키 즉시 만료
        response.addCookie(cookie);

    }

    /*
     * 프로덕션 환경 여부를 확인하는 메서드
     * @return true if production, false otherwise
     */
    private boolean isProduction() {
        // Spring 환경 변수를 사용하여 프로파일 확인
        String activeProfile = System.getProperty("spring.profiles.active", "default");
        return "prod".equalsIgnoreCase(activeProfile);
    }

}
