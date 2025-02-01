package com.safehouse.domain.user.entity;

import com.safehouse.domain.report.entity.Report;
import com.safehouse.domain.resident_community.entity.ResidentsCommunity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
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

    @OneToMany(mappedBy = "user")
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ResidentsCommunity> communities = new ArrayList<>();

    // 비밀번호 변경하면 updateAt 갱신
    public void setPassword(String password) {
        this.password = password;
        this.updatedAt = LocalDateTime.now();
    }

    public String getUserRealName() {
        return userName;
    }

    public void setUserRealName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return this.role.split("_")[0]; // ROLE_ 접두사 제거
    }

    //UserDetails로 변환하기 위해 필요
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.emailVerified;
    }


}