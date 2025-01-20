package com.safehouse.domain.resident_community.entity;

import com.safehouse.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

// ResidentsCommunity.java
@Entity
@Table(name = "residents_communities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ResidentsCommunity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityPostId;

    @Column(nullable = false)
    private String communityTitle;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String communityContent;

    @CreationTimestamp
    private LocalDateTime communityCreatedAt;

    @UpdateTimestamp
    private LocalDateTime communityUpdatedAt;

    @Column(nullable = false)
    private Long communityViews;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void increaseViews() {
        this.communityViews++;
    }

    public void update(String communityTitle, String communityContent) {
        this.communityTitle = communityTitle;
        this.communityContent = communityContent;
    }
}

