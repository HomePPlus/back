// 이메일 인증 토큰 저장
package com.safehouse.repository;

import com.safehouse.domain.User;
import com.safehouse.domain.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByEmail(String email);
    boolean existsByEmailAndVerified(String email, boolean verified);  // 추가
}
