package com.safehouse.api.inspections.inspection.dto.request;

import com.safehouse.domain.inspection.entity.InspectionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
@NoArgsConstructor
public class InspectionStatusUpdateRequest {
    @NotNull(message = "status must not be null")
    private String status;

    public InspectionStatus getInspectionStatus() {
        return Arrays.stream(InspectionStatus.values())
                .filter(s -> s.getDescription().equals(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid status: " + status));
    }
}
