package com.safehouse.api.resident_community.response;

import com.safehouse.domain.resident_community.entity.ResidentsComment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ResidentsCommentResponseDto {
    private Long residentCommentId;
    private String residentsContent;
    private String userName;
    private LocalDateTime residentCommentCreatedAt;

    public static ResidentsCommentResponseDto from(ResidentsComment savedComment) {
        return ResidentsCommentResponseDto.builder()
                .residentCommentId(savedComment.getResidentCommentId())
                .residentsContent(savedComment.getResidentContent())
                .userName(savedComment.getUser().getUserRealName())
                .residentCommentCreatedAt(savedComment.getResidentCommentCreatedAt())
                .build();
    }

    @Builder
    private ResidentsCommentResponseDto(Long residentCommentId, String residentsContent,
                                        String userName, LocalDateTime residentCommentCreatedAt) {
        this.residentCommentId = residentCommentId;
        this.residentsContent = residentsContent;
        this.userName = userName;
        this.residentCommentCreatedAt = residentCommentCreatedAt;
    }
}

