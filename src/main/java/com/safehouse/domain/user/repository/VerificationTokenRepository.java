
package com.safehouse.domain.user.repository;

import com.safehouse.domain.user.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {
    Optional<VerificationToken> findByEmailAndCode(String email, String code);

    // 추가
    boolean existsByEmailAndVerified(String email, boolean verified);
}

