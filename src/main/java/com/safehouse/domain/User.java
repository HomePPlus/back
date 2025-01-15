package com.safehouse.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String userName;    // 이름
    private String email;      // 이메일
    private String password;   // 비밀번호
    @Column(length = 11)
    private String phone;      // 번호
    private String role;       // 역할(RESIDENT/INSPECTOR/ADMIN)
    private String detailAddress;   //주소

    private boolean emailVerified = false;   // 이메일 인증 여부

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}