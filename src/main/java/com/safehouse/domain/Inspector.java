package com.safehouse.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "inspectors")
public class Inspector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inspectorId;

    //회사명 추가
    @Column(nullable = false)
    private String inspector_company;

    //사원 번호로 변경
    @Column(nullable = false)
    private String inspector_number;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
