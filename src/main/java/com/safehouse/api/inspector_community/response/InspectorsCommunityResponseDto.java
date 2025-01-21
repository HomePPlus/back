package com.safehouse.api.inspector_community.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InspectorsCommunityResponseDto {
    private Long inspectorPostId;
    private String inspectorCommunityTitle;
    private String inspectorCommunityContent;
    private String inspectorName;
    private String inspectorEmail;
    private LocalDateTime inspectorCommunityCreatedAt;
    private LocalDateTime inspectorCommunityUpdatedAt;
    private Long inspectorViews;
}