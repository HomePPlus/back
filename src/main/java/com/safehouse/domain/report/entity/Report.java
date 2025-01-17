package com.safehouse.domain.report.entity;

import com.safehouse.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(nullable = false)
    private LocalDateTime reportDate;

    @Column(nullable = false, length = 200)
    private String reportDescription;

    @Column(nullable = false)
    private String reportDetailAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  // reporter 대신 User 엔티티 참조, 관계 다시 물어보기

    //원래 점검 id도 만들어야 함
//    private long inspectionId;

    @Column(nullable = false)
    private String defectType; //여기도 원래 건물결함id 써야함

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
    private List<ReportImage> images = new ArrayList<>();  // 초기화 추가
}



