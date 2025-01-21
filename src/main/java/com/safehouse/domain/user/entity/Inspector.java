package com.safehouse.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inspectors")
public class Inspector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inspectorId;

    //회사명
    @Column(nullable = false)
    private String inspector_company;

    //사원 번호
    @Column(nullable = false)
    private String inspector_number;

    // 구역 정보 (예: 강남구, 서초구 등)
    @Column(nullable = false)
    private String area;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 점검자 이름을 User 엔티티에서 가져오는 메소드 추가
    public String getInspectorName() {
        return user.getUserRealName();  // User 엔티티의 실제 이름을 반환
    }

    public String getEmail() {
        return user.getEmail();
    }
}
