package com.safehouse.domain.resident_community.entity;

import com.safehouse.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "residents_comments")
public class ResidentsComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resident_comment_id")
    private Long residentCommentId;

    @Column(name = "resident_content", nullable = false, columnDefinition = "TEXT")
    private String residentContent;

    @Column(name = "resident_comment_created_at")
    private LocalDateTime residentCommentCreatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_post_id")
    private ResidentsCommunity residentCommunity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public ResidentsComment(String residentsContent, ResidentsCommunity residentsCommunity, User user) {
        this.residentContent = residentsContent;
        this.residentCommunity = residentsCommunity;
        this.user = user;
        this.residentCommentCreatedAt = LocalDateTime.now();
    }

    public void updateContent(String residentsContent) {
        this.residentContent = residentsContent;
    }
}