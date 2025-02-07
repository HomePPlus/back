package com.safehouse.api.checklist.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.safehouse.domain.checklist.entity.Checklist;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChecklistRequestDto {
    @NotNull(message = "점검 ID는 필수 입력값입니다")
    private String inspectionId;

    @NotNull(message = "점검 날짜는 필수 입력값입니다")
    private String inspectionDate;  // LocalDateTime에서 String으로 변경

    @NotNull(message = "점검자 이름은 필수 입력값입니다")
    private String inspectorName;

    @NotNull(message = "점검자 연락처는 필수 입력값입니다")
    private String inspectorContact;

    @NotNull(message = "주소는 필수 입력값입니다")
    private String address;

    private List<String> defectTypes;

    // 콘크리트 균열
    private String concreteCrackType;
    private String concreteCrackLengthCm;
    private String concreteCrackWidthMm;
    private String concreteCrackDepthMm;
    private String concreteCrackLeakage;
    private String concreteCrackMovement;
    private String concreteCrackChange;
    private String concreteCrackCondition;
    private String concreteCrackEmergency;
    private String concreteCrackEmergencyAction;
    private String concreteCrackRepairPlan;

    // 누수/백태
    private String leakEfloLeakageRange;
    private String leakEfloLeakageCause;
    private String leakEfloEflorescence;
    private String leakEfloImpact;
    private String leakEfloEmergency;
    private String leakEfloEmergencyAction;
    private String leakEfloRepairPlan;

    // 강재 손상
    private String steelDamageRange;
    private String steelDamageSeverity;
    private String steelDamageCause;
    private String steelDamageStabilityImpact;
    private String steelDamageEmergency;
    private String steelDamageEmergencyAction;
    private String steelDamageRepairPlan;

    // 박리
    private String delaminationRange;
    private String delaminationCause;
    private String delaminationStabilityImpact;
    private String delaminationEmergency;
    private String delaminationEmergencyAction;
    private String delaminationRepairPlan;

    // 철근 노출
    private String rebarExposureRange;
    private String rebarExposureCondition;
    private String rebarExposureCause;
    private String rebarExposureStabilityImpact;
    private String rebarExposureEmergency;
    private String rebarExposureEmergencyAction;
    private String rebarExposureRepairPlan;

    // 도장 손상
    private String paintDamageRange;
    private String paintDamageCause;
    private String paintDamageCondition;
    private String paintDamageEmergency;
    private String paintDamageEmergencyAction;
    private String paintDamageRepairPlan;

    // 필수 입력 정보
    @NotNull(message = "종합 평가는 필수 입력값입니다")
    private String overallResult;

    @NotNull(message = "모니터링 필요 여부는 필수 입력값입니다")
    private String monitoringRequired;

    private String nextInspectionDate;

    public Checklist toEntity() {
        return Checklist.builder()
                .inspectionId(inspectionId)
                .inspectionDate(inspectionDate)
                .inspectorName(inspectorName)
                .inspectorContact(inspectorContact)
                .address(address)
                .defectTypes(defectTypes)
                .concreteCrackType(concreteCrackType)
                .concreteCrackLengthCm(concreteCrackLengthCm)
                .concreteCrackWidthMm(concreteCrackWidthMm)
                .concreteCrackDepthMm(concreteCrackDepthMm)
                .concreteCrackLeakage(concreteCrackLeakage)
                .concreteCrackMovement(concreteCrackMovement)
                .concreteCrackChange(concreteCrackChange)
                .concreteCrackCondition(concreteCrackCondition)
                .concreteCrackEmergency(concreteCrackEmergency)
                .concreteCrackEmergencyAction(concreteCrackEmergencyAction)
                .concreteCrackRepairPlan(concreteCrackRepairPlan)
                .leakEfloLeakageRange(leakEfloLeakageRange)
                .leakEfloLeakageCause(leakEfloLeakageCause)
                .leakEfloEflorescence(leakEfloEflorescence)
                .leakEfloImpact(leakEfloImpact)
                .leakEfloEmergency(leakEfloEmergency)
                .leakEfloEmergencyAction(leakEfloEmergencyAction)
                .leakEfloRepairPlan(leakEfloRepairPlan)
                .steelDamageRange(steelDamageRange)
                .steelDamageSeverity(steelDamageSeverity)
                .steelDamageCause(steelDamageCause)
                .steelDamageStabilityImpact(steelDamageStabilityImpact)
                .steelDamageEmergency(steelDamageEmergency)
                .steelDamageEmergencyAction(steelDamageEmergencyAction)
                .steelDamageRepairPlan(steelDamageRepairPlan)
                .delaminationRange(delaminationRange)
                .delaminationCause(delaminationCause)
                .delaminationStabilityImpact(delaminationStabilityImpact)
                .delaminationEmergency(delaminationEmergency)
                .delaminationEmergencyAction(delaminationEmergencyAction)
                .delaminationRepairPlan(delaminationRepairPlan)
                .rebarExposureRange(rebarExposureRange)
                .rebarExposureCondition(rebarExposureCondition)
                .rebarExposureCause(rebarExposureCause)
                .rebarExposureStabilityImpact(rebarExposureStabilityImpact)
                .rebarExposureEmergency(rebarExposureEmergency)
                .rebarExposureEmergencyAction(rebarExposureEmergencyAction)
                .rebarExposureRepairPlan(rebarExposureRepairPlan)
                .paintDamageRange(paintDamageRange)
                .paintDamageCause(paintDamageCause)
                .paintDamageCondition(paintDamageCondition)
                .paintDamageEmergency(paintDamageEmergency)
                .paintDamageEmergencyAction(paintDamageEmergencyAction)
                .paintDamageRepairPlan(paintDamageRepairPlan)
                .overallResult(overallResult)
                .monitoringRequired(monitoringRequired)
                .nextInspectionDate(nextInspectionDate)
                .build();
    }
}

