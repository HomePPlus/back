package com.safehouse.api.resident_community.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResidentsCommentRequestDto {
    private String residentsContent;

    @Builder
    public ResidentsCommentRequestDto(String residentsContent) {
        this.residentsContent = residentsContent;
    }
}
