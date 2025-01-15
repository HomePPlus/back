package com.safehouse.repository;

import com.safehouse.domain.Inspector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InspectorRepository extends JpaRepository<Inspector, Long> {
    // 부서별 관리자 목록 조회
    //List<Admin> findByDepartment(String department);

    // 특정 유저의 관리자 정보 조회
    //Optional<Admin> findByUserId(Long userId);

    // 자격증 정보로 관리자 검색
    //List<Admin> findByCertificationInfoContaining(String certificationInfo);

    // 부서별 관리자 수 조회
    //Long countByDepartment(String department);

    // 특정 유저의 관리자 정보 존재 여부 확인
    //boolean existsByUserId(Long userId);
}
