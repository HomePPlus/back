package com.safehouse.domain.dashboard.defect.service;
import com.safehouse.api.dashboard.defect.response.DefectStatsResponse;
import com.safehouse.common.exception.CustomException;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.dashboard.defect.entity.DefectType;
import com.safehouse.domain.dashboard.defect.repository.DefectStatProjection;
import com.safehouse.domain.dashboard.defect.repository.ReportDefectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportDefectStatService {

    private final ReportDefectRepository reportDefectRepository;
    private static final List<String> VALID_AREAS = List.of(
            "부산시", "동래구", "해운대구", "수영구", "사하구",
            "부산진구", "남구", "북구", "강서구", "연제구", "사상구", "금정구",
            "동구", "서구", "영도구", "중구", "기장군"
    );

    public ApiResponse<DefectStatsResponse> getDefectStats(String area) {
        validateArea(area);

        List<DefectStatProjection> results = reportDefectRepository.countDefectsByArea(area);
        if(results.isEmpty()) {
            throw new CustomException.NotFoundException("해당 지역의 통계 데이터가 없습니다");
        }

        return ApiResponse.ok(
                DefectStatsResponse.builder()
                        .areaType(area.equals("부산시") ? "ALL" : "DISTRICT")
                        .defectCounts(processDefectTypes(results))
                        .build()
        );
    }

    private Map<String, Long> processDefectTypes(List<DefectStatProjection> results) {
        Map<String, Long> mergedStats = new HashMap<>();

        results.forEach(proj -> {
            String mappedType = DefectType.mapDefectType(proj.getDefectType());
            mergedStats.merge(mappedType, proj.getCount(), Long::sum);
        });

        return initializeCategories(mergedStats);
    }

    private Map<String, Long> initializeCategories(Map<String, Long> stats) {
        for (DefectType type : DefectType.values()) {
            stats.putIfAbsent(type.getDisplayName(), 0L); // getter 사용
        }
        return stats;
    }

    private void validateArea(String area) {
        if (!VALID_AREAS.contains(area)) {
            throw new CustomException.BadRequestException("유효하지 않은 지역입니다");
        }
    }
}