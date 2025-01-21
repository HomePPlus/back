package com.safehouse.api.resident_community.response;

import com.safehouse.domain.resident_community.entity.ResidentsCommunity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentsCommunityResponseDto {
    private Long communityPostId;
    private String communityTitle;
    private String communityContent;
    private String userName;
    private String userEmail;
    private LocalDateTime communityCreatedAt;
    private LocalDateTime communityUpdatedAt;
    private Long communityViews;

    public static ResidentsCommunityResponseDto from(ResidentsCommunity community) {
        ResidentsCommunityResponseDto dto = new ResidentsCommunityResponseDto();
        dto.communityPostId = community.getCommunityPostId();
        dto.communityTitle = community.getCommunityTitle();
        dto.communityContent = community.getCommunityContent();
        dto.userName = community.getUser().getUserRealName();
        dto.userEmail = community.getUser().getEmail();
        dto.communityCreatedAt = community.getCommunityCreatedAt();
        dto.communityUpdatedAt = community.getCommunityUpdatedAt();
        dto.communityViews = community.getCommunityViews();
        return dto;
    }
}
