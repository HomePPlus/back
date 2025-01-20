package com.safehouse.api.resident_community.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// ResidentsCommunityRequestDto.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentsCommunityRequestDto {
    @NotBlank(message = "제목은 필수입니다")
    private String communityTitle;

    @NotBlank(message = "내용은 필수입니다")
    private String communityContent;
}

