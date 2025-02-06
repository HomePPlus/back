package com.safehouse.common.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.safehouse.api.checklist.request.ChecklistRequestDto;
import com.safehouse.api.checklist.response.FastApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class FastApiClient {

    private final RestTemplate restTemplate;

    @Value("${fastapi_checklist.server.url}")
    private String fastApiUrl;

    public FastApiResponseDto submitChecklist(ChecklistRequestDto requestDto) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

            // 기본 정보
            formData.add("inspection_id", requestDto.getInspectionId());
            formData.add("inspection_date", requestDto.getInspectionDate());
            formData.add("inspector_name", requestDto.getInspectorName());
            formData.add("inspector_contact", requestDto.getInspectorContact());
            formData.add("address", requestDto.getAddress());

            if (requestDto.getDefectTypes() != null) {
                requestDto.getDefectTypes().forEach(type -> formData.add("defect_types", type));
            }

            // 콘크리트 균열
            formData.add("concrete_crack_type", requestDto.getConcreteCrackType());
            formData.add("concrete_crack_length_cm", requestDto.getConcreteCrackLengthCm());
            formData.add("concrete_crack_width_mm", requestDto.getConcreteCrackWidthMm());
            formData.add("concrete_crack_depth_mm", requestDto.getConcreteCrackDepthMm());
            formData.add("concrete_crack_leakage", requestDto.getConcreteCrackLeakage());
            formData.add("concrete_crack_movement", requestDto.getConcreteCrackMovement());
            formData.add("concrete_crack_change", requestDto.getConcreteCrackChange());
            formData.add("concrete_crack_condition", requestDto.getConcreteCrackCondition());
            formData.add("concrete_crack_emergency", requestDto.getConcreteCrackEmergency());
            formData.add("concrete_crack_emergency_action", requestDto.getConcreteCrackEmergencyAction());
            formData.add("concrete_crack_repair_plan", requestDto.getConcreteCrackRepairPlan());

            // 누수/백태
            formData.add("leak_eflo_leakage_range", requestDto.getLeakEfloLeakageRange());
            formData.add("leak_eflo_leakage_cause", requestDto.getLeakEfloLeakageCause());
            formData.add("leak_eflo_eflorescence", requestDto.getLeakEfloEflorescence());
            formData.add("leak_eflo_impact", requestDto.getLeakEfloImpact());
            formData.add("leak_eflo_emergency", requestDto.getLeakEfloEmergency());
            formData.add("leak_eflo_emergency_action", requestDto.getLeakEfloEmergencyAction());
            formData.add("leak_eflo_repair_plan", requestDto.getLeakEfloRepairPlan());

            // 강재 손상
            formData.add("steel_damage_range", requestDto.getSteelDamageRange());
            formData.add("steel_damage_severity", requestDto.getSteelDamageSeverity());
            formData.add("steel_damage_cause", requestDto.getSteelDamageCause());
            formData.add("steel_damage_stability_impact", requestDto.getSteelDamageStabilityImpact());
            formData.add("steel_damage_emergency", requestDto.getSteelDamageEmergency());
            formData.add("steel_damage_emergency_action", requestDto.getSteelDamageEmergencyAction());
            formData.add("steel_damage_repair_plan", requestDto.getSteelDamageRepairPlan());

            // 박리
            formData.add("delamination_range", requestDto.getDelaminationRange());
            formData.add("delamination_cause", requestDto.getDelaminationCause());
            formData.add("delamination_stability_impact", requestDto.getDelaminationStabilityImpact());
            formData.add("delamination_emergency", requestDto.getDelaminationEmergency());
            formData.add("delamination_emergency_action", requestDto.getDelaminationEmergencyAction());
            formData.add("delamination_repair_plan", requestDto.getDelaminationRepairPlan());

            // 철근 노출
            formData.add("rebar_exposure_range", requestDto.getRebarExposureRange());
            formData.add("rebar_exposure_condition", requestDto.getRebarExposureCondition());
            formData.add("rebar_exposure_cause", requestDto.getRebarExposureCause());
            formData.add("rebar_exposure_stability_impact", requestDto.getRebarExposureStabilityImpact());
            formData.add("rebar_exposure_emergency", requestDto.getRebarExposureEmergency());
            formData.add("rebar_exposure_emergency_action", requestDto.getRebarExposureEmergencyAction());
            formData.add("rebar_exposure_repair_plan", requestDto.getRebarExposureRepairPlan());

            // 도장 손상
            formData.add("paint_damage_range", requestDto.getPaintDamageRange());
            formData.add("paint_damage_cause", requestDto.getPaintDamageCause());
            formData.add("paint_damage_condition", requestDto.getPaintDamageCondition());
            formData.add("paint_damage_emergency", requestDto.getPaintDamageEmergency());
            formData.add("paint_damage_emergency_action", requestDto.getPaintDamageEmergencyAction());
            formData.add("paint_damage_repair_plan", requestDto.getPaintDamageRepairPlan());

            // 종합 평가
            formData.add("overall_result", requestDto.getOverallResult());
            formData.add("monitoring_required", requestDto.getMonitoringRequired());
            formData.add("next_inspection_date", requestDto.getNextInspectionDate());

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

            // 로깅 추가
            System.out.println("Sending form data to FastAPI: " + formData);

            // 요청 전송 및 응답 받기
            FastApiResponseDto response = restTemplate.postForObject(
                    fastApiUrl + "/submit_checklist",
                    request,
                    FastApiResponseDto.class
            );

            // 응답 로깅
            System.out.println("FastAPI Response: " + response);

            return response;
        } catch (Exception e) {
            System.err.println("Error submitting checklist: " + e.getMessage());
            throw new RuntimeException("Failed to submit checklist", e);
        }
    }

    public byte[] downloadPdfFromUrl(String reportId) {
        String downloadUrl = fastApiUrl + "/download/" + reportId;
        ResponseEntity<byte[]> response = restTemplate.getForEntity(
                downloadUrl,
                byte[].class
        );
        return response.getBody();
    }
}