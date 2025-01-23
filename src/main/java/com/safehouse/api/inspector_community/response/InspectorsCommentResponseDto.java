package com.safehouse.api.inspector_community.response;

import com.safehouse.domain.inspector_community.entity.InspectorsComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class InspectorsCommentResponseDto {
    private Long inspectorsCommentId;
    private String inspectorsContent;
    private LocalDateTime inspectorsCommentCreatedAt;
    private Long inspectorsPostId;
    private String username;
    private Long userId;

    public static InspectorsCommentResponseDto from(InspectorsComment inspectorsComment) {
        return InspectorsCommentResponseDto.builder()
                .inspectorsCommentId(inspectorsComment.getInspectorCommentId())
                .inspectorsContent(inspectorsComment.getInspectorContent())
                .inspectorsCommentCreatedAt(inspectorsComment.getInspectorCommentCreatedAt())
                .inspectorsPostId(inspectorsComment.getInspectorsCommunity().getInspectorPostId())
                .username(inspectorsComment.getUser().getUsername())
                .userId(inspectorsComment.getUser().getUserId())
                .build();
    }
}


