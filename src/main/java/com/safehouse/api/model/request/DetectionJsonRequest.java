package com.safehouse.api.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString  // 디버깅을 위해 추가
public class DetectionJsonRequest {
    @NotNull(message = "파일명은 필수입니다")  // validation 추가
    private String file1;
}

