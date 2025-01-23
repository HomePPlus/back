package com.safehouse.domain.inspector_community.entity;

import com.safehouse.domain.user.entity.Inspector;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inspectors_communities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InspectorsCommunity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inspectorPostId;

    @Column(nullable = false, length = 200)
    private String inspectorCommunityTitle;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String inspectorCommunityContent;

    @Column(nullable = false)
    private Long inspectorViews;

    @CreationTimestamp
    private LocalDateTime inspectorCommunityCreatedAt;

    @UpdateTimestamp
    private LocalDateTime inspectorCommunityUpdatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspector_id")
    private Inspector inspector;  // User를 Inspector로 변경

    @Builder
    public InspectorsCommunity(String inspectorCommunityTitle, String inspectorCommunityContent, Long inspectorViews, Inspector inspector) {
        this.inspectorCommunityTitle = inspectorCommunityTitle;
        this.inspectorCommunityContent = inspectorCommunityContent;
        this.inspectorViews = inspectorViews;
        this.inspector = inspector;
    }

    public void update(String inspectorCommunityTitle, String inspectorCommunityContent) {
        this.inspectorCommunityTitle = inspectorCommunityTitle;
        this.inspectorCommunityContent = inspectorCommunityContent;
    }

    public void increaseViews() {
        this.inspectorViews++;
    }

    //댓글
    @OneToMany(mappedBy = "inspectorsCommunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InspectorsComment> comments = new ArrayList<>();

    public void addComment(InspectorsComment comment) {
        this.comments.add(comment);
    }

    public void removeComment(InspectorsComment comment) {
        this.comments.remove(comment);
    }
}