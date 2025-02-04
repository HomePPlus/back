
package com.safehouse.domain.user.repository;

import com.safehouse.domain.user.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {
    Optional<VerificationToken> findByEmailAndCode(String email, String code);

    boolean existsByEmailAndVerified(String email, boolean verified);

    Optional<VerificationToken> findByEmail(String email);

    List<VerificationToken> findByExpiryDateBeforeAndVerifiedFalse(LocalDateTime now);

    @Modifying
    @Transactional
    void deleteByEmail(String email);
}

