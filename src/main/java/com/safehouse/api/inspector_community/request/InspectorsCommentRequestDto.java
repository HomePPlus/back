package com.safehouse.api.inspector_community.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InspectorsCommentRequestDto {
    @NotNull(message = "댓글 내용은 필수입니다")
    @NotBlank(message = "댓글 내용은 비워둘 수 없습니다")
    private String inspectorsContent;
}
