package com.safehouse.domain.inspector_community.entity;

import com.safehouse.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "inspectors_comments")
public class InspectorsComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inspectors_comment_id")
    private Long inspectorCommentId;

    @Column(name = "inspectors_content", nullable = false, columnDefinition = "TEXT")
    private String inspectorContent;

    @Column(name = "inspectors_comment_created_at")
    private LocalDateTime inspectorCommentCreatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspectors_post_id")
    private InspectorsCommunity inspectorsCommunity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public InspectorsComment(String inspectorsContent, InspectorsCommunity inspectorsCommunity, User user) {
        this.inspectorContent = inspectorsContent;
        this.inspectorsCommunity = inspectorsCommunity;
        this.user = user;
        this.inspectorCommentCreatedAt = LocalDateTime.now();
    }

    public void updateContent(String inspectorsContent) {
        this.inspectorContent = inspectorsContent;
    }
}
