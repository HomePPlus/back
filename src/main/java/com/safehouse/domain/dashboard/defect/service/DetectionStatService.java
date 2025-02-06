package com.safehouse.domain.dashboard.defect.service;

import com.safehouse.api.model.response.DetectionResponse;
import com.safehouse.common.exception.CustomException;
import com.safehouse.common.response.ApiResponse;
import com.safehouse.domain.model.entity.DetectionResult;
import com.safehouse.domain.model.repository.DetectionResultRepository;
import com.safehouse.domain.report.entity.Report;
import com.safehouse.domain.dashboard.defect.entity.DefectType;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DetectionStatService {

    private final DetectionResultRepository detectionResultRepository;

    private static final List<String> VALID_AREAS = List.of(
            "부산시", "동래구", "해운대구", "수영구", "사하구",
            "부산진구", "남구", "북구", "강서구", "연제구", "사상구", "금정구",
            "동구", "서구", "영도구", "중구", "기장군"
    );

    public ApiResponse<Map<String, Map<String, Long>>> getDetectionStats(String area) {
        validateArea(area);

        List<Object[]> results = detectionResultRepository.getDetectionResultsByArea(area);
        if(results.isEmpty()) {
            throw new CustomException.NotFoundException("해당 지역의 통계 데이터가 없습니다");
        }

        Map<String, Map<String, Long>> stats = new HashMap<>();
        for (Object[] result : results) {
            String areaName = (String) result[0];
            String detectionJson = (String) result[1];

            if (!stats.containsKey(areaName)) {
                stats.put(areaName, new HashMap<>());
                for (DefectType type : DefectType.values()) {
                    if (!type.getDisplayName().equals("모름")) { // "모름" 항목 제외
                        stats.get(areaName).put(type.getDisplayName(), 0L);
                    }
                }
                stats.get(areaName).put("분석 결과 없음", 0L);
            }

            try {
                JSONObject jsonObject = new JSONObject(detectionJson);
                JSONArray detections = jsonObject.getJSONArray("detections");

                if (detections.isEmpty()) {
                    stats.get(areaName).put("분석 결과 없음", stats.get(areaName).get("분석 결과 없음") + 1);
                } else {
                    Set<String> detectedTypes = new HashSet<>(); // 탐지된 결함 유형을 저장할 집합

                    for (int i = 0; i < detections.length(); i++) {
                        JSONObject detection = detections.getJSONObject(i);
                        String label = detection.getString("label");

                        // 결함 유형 매핑
                        String mappedType = mapDefectType(label);

                        if (!detectedTypes.contains(mappedType)) { // 이미 카운트되지 않은 결함 유형만 추가
                            detectedTypes.add(mappedType);
                            if (stats.get(areaName).containsKey(mappedType)) {
                                stats.get(areaName).put(mappedType, stats.get(areaName).get(mappedType) + 1);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // JSON 파싱 오류 발생 시 로깅
                System.out.println("JSON 파싱 오류: " + e.getMessage());
            }
        }

        return ApiResponse.ok(stats);
    }

    private String mapDefectType(String originalType) {
        return switch (originalType) {
            case "Exposure" -> DefectType.REBAR_EXPOSURE.getDisplayName();
            case "Spalling" -> DefectType.PEELING.getDisplayName();
            case "PaintDamage" -> DefectType.PAINT_DAMAGE.getDisplayName();
            case "CoatingDamage" -> DefectType.PAINT_DAMAGE.getDisplayName();
            case "SteelDefect_Level1", "SteelDefect_Level2", "SteelDefect_Level3" -> DefectType.STEEL_DAMAGE.getDisplayName();
            case "crack_1", "crack_2", "crack_3" -> DefectType.CRACK.getDisplayName();
            case "Efflorescence_Level1", "Efflorescence_Level2", "Efflorescence_Level3" -> DefectType.LEAK_WHITENING.getDisplayName();
            default -> DefectType.UNKNOWN.getDisplayName();
        };
    }

    private void validateArea(String area) {
        if (!VALID_AREAS.contains(area)) {
            throw new CustomException.BadRequestException("유효하지 않은 지역입니다");
        }
    }
}