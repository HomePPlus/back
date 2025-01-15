package com.safehouse.repository;

import com.safehouse.domain.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {
    // JpaRepository<Resident, Long>에서:
    // Resident: 엔티티 타입
    // Long: 기본키(ID) 타입
}
