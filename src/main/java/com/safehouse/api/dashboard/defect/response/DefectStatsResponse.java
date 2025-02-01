package com.safehouse.api.dashboard.defect.response;

import lombok.Builder;
import lombok.Getter;
import java.util.Map;

@Getter
@Builder
public class DefectStatsResponse {
    private String areaType;
    private Map<String, Long> defectCounts;
}
